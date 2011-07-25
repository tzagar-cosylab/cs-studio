package org.csstudio.webopi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

import org.csstudio.opibuilder.persistence.URLPath;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.OPIRunnerPerspective.Position;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.RequestUtil;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Configures the initial size and appearance of a workbench window.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    
    @SuppressWarnings("deprecation")
	@Override
    public void postWindowCreate() {
    	super.postWindowCreate();
    	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    	Shell shell = configurer.getWindow().getShell();
    	shell.setText("WebOPI");
    	shell.setMaximized(true);
		IPath path = RequestUtil.getOPIPathFromRequest();
		if(path == null)
			return;
		 if(!RequestUtil.isStandaloneMode())			 		
			RunModeService.getInstance().runOPI(path, 
    			TargetWindow.SAME_WINDOW, null);
    	
    }
    
    public void preWindowOpen() {
    	
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();    
       
        configurer.setShowCoolBar(!RequestUtil.isStandaloneMode());
        configurer.setShowMenuBar(false);
        configurer.setShowStatusLine(false);        
        configurer.setTitle("WebOPI");

        configurer.setShellStyle(SWT.NO_TRIM);
    }
    
   
    
}
