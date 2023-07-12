import ij.IJ
import ij.ImagePlus
import ij.gui.ShapeRoi
import ij.measure.ResultsTable
import ij.plugin.ChannelSplitter
import ij.plugin.frame.RoiManager
import inra.ijpb.binary.BinaryImages


// INPUT UI
//
#@File(label = "Input File Directory", style = "directory") inputFilesDir
#@File(label = "Output directory", style = "directory") outputDir
#@Boolean(label = "Apply DAPI?") applyDAPI


// IDE
//
//
//def headless = true;
//new ImageJ().setVisible(true);

IJ.log("-Parameters selected: ")
IJ.log("    -inputFileDir: " + inputFilesDir)
IJ.log("    -outputDir: " + outputDir)
IJ.log("                                                           ");
/** Get files (images) from input directory */
def listOfFiles = inputFilesDir.listFiles();
/** Define table to save results */
def table = new ResultsTable();

for (def i = 0; i < listOfFiles.length; i++) {

    def rm = new RoiManager(false);

    if (!listOfFiles[i].getName().contains("DS")) {
        IJ.log("Analyzing file: " + listOfFiles[i].getName());
        /** Define output directory per file */
        def outputImageDir = new File(outputDir.getAbsolutePath());

        if (!outputImageDir.exists()) {
            def results = false;

            try {
                outputImageDir.mkdir();
                results = true;
            } catch (SecurityException se) {
            }
        }
        IJ.log("    -Creating output directory in:" +
                outputDir.getAbsolutePath());

        /** Import tiff file*/
        IJ.log("        -Analyzing image: " + listOfFiles[i].getName())
        /** Declare each image to process within input directory */
        def imp = IJ.openImage(listOfFiles[i].getAbsolutePath())
        def impTitle = null;
        if (imp.getTitle().contains("/")) {
            impTitle = imp.getTitle().replaceAll("/", "");
        } else {
            impTitle = imp.getTitle();
        }

        /** Get calibration from non-transformed image. */
        def cal = imp.getCalibration();

        /** Split channels */
        def channels = ChannelSplitter.split(imp);
        if (channels.length == 4) {
            /** Get channel Red */
            def chRed = channels[0];
            /** Get channel Green */
            def chGreen = channels[1];
            /** Get channel Blue */
            def chBlue = channels[2];
            /** Get channel Magenta */
            def chMagenta = channels[3];

            /** Segment Red Areas */
            IJ.run(chRed, "Auto Threshold", "method=Huang ignore_black white");
            /** Apply median filter to remove background areas below
             3 pixels */
            IJ.run(chRed, "Median...", "radius=3")
            /** Apply area opening in binary images */
            chRed = new ImagePlus("", BinaryImages.areaOpening(chRed.getProcessor(), 250));
            /** Create contour selection on red areas */
            IJ.run(chRed, "Create Selection", "");
            /** Get contour as ROI */
            def roiRed = chRed.getRoi();


            /** Segment Green Areas */
            IJ.run(chGreen, "Auto Threshold", "method=Otsu ignore_black white");
            /** Apply median filter to remove background areas below
             1 pixels */
            IJ.run(chGreen, "Median...", "radius=3")
            /** Create contour selection on red areas */
            IJ.run(chGreen, "Create Selection", "");
            /** Get contour as ROI */
            def roiGreen = chGreen.getRoi();

            /** Segment Magenta Areas */
            IJ.run(chMagenta, "Auto Threshold", "method=Otsu ignore_black white");
            /** Apply median filter to remove background areas below
             3 pixels */
            IJ.run(chMagenta, "Median...", "radius=5")
            /** Create contour selection on magenta areas */
            IJ.run(chMagenta, "Create Selection", "");
            /** Get contour as ROI */
            def roiMagenta = chMagenta.getRoi();

            /** Check DAPI or not */
            if (applyDAPI) {
                /** Segment Blue Areas */
                IJ.run(chBlue, "Auto Threshold", "method=Otsu ignore_black white");
                /** Apply median filter to remove background areas below
                 3 pixels */
                IJ.run(chBlue, "Median...", "radius=3")
                /** Create contour selection on blue areas */
                IJ.run(chBlue, "Create Selection", "");
                /** Get contour as ROI */
                def roiBlue = chBlue.getRoi();

                /** Apply AND operator to keep those blue areas overlapping with red areas */
                def roiBlueRed = new ShapeRoi(roiBlue).and(new ShapeRoi(roiRed)).shapeToRoi();
                /** Apply AND operator to keep those blue-red  areas overlapping with green areas */
                def roiBlueRedGreen = new ShapeRoi(roiBlueRed).and(new ShapeRoi(roiGreen)).shapeToRoi();
                /** Apply AND operator to keep those blue areas overlapping with magenta areas */
                def roiBlueMagenta = new ShapeRoi(roiBlue).and(new ShapeRoi(roiMagenta)).shapeToRoi();
                /** Apply AND operator to keep those blue-magenta  areas overlapping with green areas */
                def roiBlueMagentaGreen = new ShapeRoi(roiBlueMagenta).and(new ShapeRoi(roiGreen)).shapeToRoi();


                /** Get Blue Area in pixels */
                def areaBlue = roiBlue.getStatistics().area;
                /** Get Blue-Red Area in pixels */
                def areaBlueRed = roiBlueRed.getStatistics().area;
                /** Get Green within Blue-Red Area in pixels */
                def areaBlueRedGreen = roiBlueRedGreen.getStatistics().area;
                /** Get Magenta Area in pixels */
                def areaMagenta = roiMagenta.getStatistics().area;
                /** Get Blue-Magenta Area in pixels */
                def areaBlueMagenta = roiBlueMagenta.getStatistics().area;
                /** Get Green within Blue-Magenta Area in pixels */
                def areaBlueMagentaGreen = roiBlueMagentaGreen.getStatistics().area;

                rm.addRoi(roiBlue)
                rm.addRoi(roiBlueRed)
                rm.addRoi(roiBlueRedGreen)
                rm.addRoi(roiMagenta)
                rm.addRoi(roiBlueMagenta)
                rm.addRoi(roiBlueMagentaGreen)
                rm.runCommand("Save",
                        outputDir.getAbsolutePath() + File.separator + imp.getShortTitle() + "_RoiSet.zip")
                rm.reset()

                table.incrementCounter();
                table.setValue("Image Title", i, listOfFiles[i].getName())
                table.setValue("Blue Area", i, areaBlue.toString())
                table.setValue("Red overlap. Blue", i, areaBlueRed.toString())
                table.setValue("Blue-Red overlap. Green", i, areaBlueRedGreen.toString())
                table.setValue("Magenta Area", i, areaMagenta.toString())
                table.setValue("Magenta overlap. Blue", i, areaBlueMagenta.toString())
                table.setValue("Blue-Magenta overlap. Green", i, areaBlueMagentaGreen.toString())

                table.setValue("Normalized Ratio Green vs. Blue-Red Area", i, Double.valueOf(areaBlueRedGreen / areaBlueRed).toString())
                table.setValue("% Ratio Green vs. Blue-Red Area", i, Double.valueOf((areaBlueRedGreen * 100) / areaBlueRed).toString())
                table.setValue("Normalized Ratio Green vs. Blue-Magenta Area", i, Double.valueOf(areaBlueMagentaGreen / areaBlueMagenta).toString())
                table.setValue("% Ratio Green vs. Blue-Magenta Area", i, Double.valueOf((areaBlueMagentaGreen * 100) / areaBlueMagenta).toString())


            } else {
            if(roiRed != null && roiGreen != null){
                /** Apply AND operator to keep those red areas overlapping with green areas */
                def roiRedGreen = new ShapeRoi(roiRed).and(new ShapeRoi(roiGreen)).shapeToRoi();
                /** Apply AND operator to keep those magenta areas overlapping with green areas */
                def roiMagentaGreen = new ShapeRoi(roiMagenta).and(new ShapeRoi(roiGreen)).shapeToRoi();

                /** Get Red Area in pixels */
                def areaRed = roiRed.getStatistics().area;
                def areaRedGreen = null
                if (roiRedGreen != null) {
                    /** Get Green within Red Area in pixels */
                    areaRedGreen = roiRedGreen.getStatistics().area;
                }else{
                    areaRedGreen = 0.0
                }
                /** Get Magenta Area in pixels */
                def areaMagenta = roiMagenta.getStatistics().area;
                def areaMagentaGreen = null
                if (roiMagentaGreen != null) {
                    /** Get Green within Red Area in pixels */
                    areaMagentaGreen = roiMagentaGreen.getStatistics().area;
                }else{
                    areaMagentaGreen = 0.0
                }

                rm.addRoi(roiRed)
                if (roiRedGreen != null)
                    rm.addRoi(roiRedGreen)
                rm.addRoi(roiMagenta)
                if (roiMagentaGreen != null)
                    rm.addRoi(roiMagentaGreen)
                rm.runCommand("Save",
                        outputDir.getAbsolutePath() + File.separator + imp.getShortTitle() + "_RoiSet.zip")
                rm.reset()

                table.incrementCounter();
                table.setValue("Image Title", i, listOfFiles[i].getName())
                table.setValue("Red Area", i, areaRed.toString())
                table.setValue("Red overlap. Green", i, areaRedGreen.toString())
                table.setValue("Magenta Area", i, areaMagenta.toString())
                table.setValue("Magenta overlap. Green", i, areaMagentaGreen.toString())


                table.setValue("Normalized Ratio Green vs. Red Area", i, Double.valueOf(areaRedGreen / areaRed).toString())
                table.setValue("% Ratio Green vs. Red Area", i, Double.valueOf((areaRedGreen * 100) / areaRed).toString())
                table.setValue("Normalized Ratio Green vs.Magenta Area", i, Double.valueOf(areaMagentaGreen / areaMagenta).toString())
                table.setValue("% Ratio Green vs. Magenta Area", i, Double.valueOf((areaMagentaGreen * 100) / areaMagenta).toString())

                }
            }


        } else {
            IJ.log("It is needed to have at least 4 channels to do the analysis.")
        }

    }
}
def tablePath = new File(outputDir, "table_results" + ".csv").toString(); IJ.log("Saving table: " + tablePath + " in " + outputDir.getAbsolutePath()); table.save(tablePath);



IJ.log("Done!!!")
