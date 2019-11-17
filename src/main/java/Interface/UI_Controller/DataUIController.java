package Interface.UI_Controller;

import ArchiveLoader.FilesArchive;
import Interface.UI.DataUI;
import Utils.Constants;

import java.util.Map;

import static Main.Launcher.*;

public class DataUIController extends DataUI {


    public void createTabs(Map<String, FilesArchive> m){
        if(!m.isEmpty()){
            for (FilesArchive f: m.values()){
                getDataTabs().add(f.getName(), new DataPanel(f));
            }
        }else{
            mainUI.disableButton(Constants.UIVE.FILES_BUTTON_NAME);
        }
    }
}
