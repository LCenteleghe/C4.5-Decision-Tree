package util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Utils {
    private static File lastFile = null;

    public static File filePicker(String title, String extDescription, String... extFilters) {
	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter(extDescription, extFilters);
	chooser.setCurrentDirectory(lastFile);
	chooser.setFileFilter(filter);
	chooser.setDialogTitle(title);
	int returnVal = chooser.showOpenDialog(null);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    lastFile = chooser.getSelectedFile();
	    return chooser.getSelectedFile();
	}
	return null;
    }
}
