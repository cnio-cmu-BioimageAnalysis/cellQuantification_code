# cellQuantification_code
This Groovy script helps to quantify positive areas of overlapping among cells, having as output the corresponding rois together with a quantification summary of positive areas of overlapping.
## Download cellQuantification_code
1. Go to the ``GitHub`` repository
2. Click on ``<Code>``>``Download ZIP``
3. The repo will be found at ``Downloads`` directory.
## Requirements
-Activate ``IJPB-plugins`` from the ImageJ Update Site: ``Help``>``Update``>``Manage Update Site``
 <p align="center">
    <img width="600" height="340" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/cce12d4f-f071-45bc-9bec-1f6b5f1c4674">
    </p>

## Running cellQuantification_code in headless mode through ImageJ/Windows Windows Terminal (ALL parameters)

``ImageJ-win64.exe --ij2 --headless --run "/absolute_path/to/groovyscript/cellQuantification.groovy" "headless=true, inputFilesDir='/absolute_path/to/inputFiles/images',outputDir='/absolute_path/to/outputDirectory/results',applyDAPI=false,"``
### Parameters Explanation:
- ``headless`` : true. 
- ``inputFilesDir`` : Directory in which the images (tiff, jpeg... files) to be analyzed are located. ``'/home/anaacayuela/Ana_pruebas_imageJ/margarita/images'``.
- ``outputDir`` : Directory in which the outputs are saved. ``'/home/anaacayuela/Ana_pruebas_imageJ/margarita/results'``
- ``applyDAPI`` : To initially identify and further take into account those areas of interest which colocalize with DAPI. Values allowed: ``true`` or ``false``.
## Running through ImageJ/Fiji 
1. Navigate to reach Script Editor tool:
   - By writing ``true`` on the search tool or by ``File``>``New``>``Script...``
     <p align="center">
    <img width="650" height="350" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/0ad85b7b-d214-41a1-83a3-ac4c9395231b">
    </p>

2. Browse to find the directory in which the corresponding the groovy script is stored: ``cellQuantification.groovy``
    <p align="center">
    <img width="500" height="350" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/5b34dde0-2f35-4908-85f2-ffc4f89341d5">
    </p>
 
3. Press ``Run`` button to compile the script.
    <p align="center">
    <img width="500" height="350" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/1886af45-c01a-44d3-804b-30e289a2aa38">
    </p>

4. Then a dialog will be displayed in order to set both the input directory path in which the images to be analyzed are stored and the output directory path to save the outputs.
   <p align="center">
    <img width="400" height="150" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/b9948f92-942a-468d-b2df-b994e0aa9f85">
    </p>

5. A log window will appear to update about the processing status.
  <p align="center">
    <img width="350" height="150" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/ae08ebc2-a720-451c-8a50-542a708972fa">
    </p>
 
6. Finally, you will be enabled to check the outputs (``CSV table`` and ``ZIP RoiSets`` corresponding for each area of interest) in the output directory previously selected.
  <p align="center">
    <img width="600" height="80" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/bcd520f9-fed0-44f6-aade-757450d05539">
    </p>

- [x] To check ``ZIP RoiSets`` through ImageJ/Fij, **NOTE THAT** you do not have to unzip these files. Go to ``RoiManager``>``More``>``Open``>``imageTitle_RoiSet``
<p align="center">
    <img width="120" height="150" src="https://github.com/cnio-cmu-BioimageAnalysis/cellQuantification_code/assets/83207172/d6d9a16c-14c8-4867-b925-cfe7031cb12e">
    </p>






