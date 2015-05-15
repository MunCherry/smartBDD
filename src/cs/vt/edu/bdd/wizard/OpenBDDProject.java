package cs.vt.edu.bdd.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import cs.vt.edu.bdd.Activator;

/**
 * ¸ 
 * 
 * @author Tung
 *
 */
public class OpenBDDProject extends Wizard implements INewWizard {

	protected IStructuredSelection selection;
	protected IWorkbench workbench;	
	
	private IProject 		activeProject;
	
	
	private ProjectSelectionPage		selectionPage;
	
		
	/**
	 * Add pages to wizard.
	 */
	public void addPages()
	{	
		selectionPage = new ProjectSelectionPage(
				"cs.vt.edu.bdd.wizard.ProjectSelection",
				workbench,
				selection);
		
		selectionPage.setTitle("Select Your Project");
		selectionPage.setDescription("Please choose your project that contains scenarios!");
		addPage(selectionPage);
		
	}
	
	public IProject getActiveProject(){
		return activeProject;
	}	

	
//	@Override
	public boolean performFinish() 
	{
		try{
			getContainer().run(false, false, new WorkspaceModifyOperation() {
				
				protected void execute(IProgressMonitor monitor) {
					doFinish(monitor);
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

//	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		this.workbench = workbench;
		this.selection = selection;
		
		activeProject = null;
		
		setWindowTitle("Project Selection Wizard");
		
		ImageDescriptor imageDesc = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID,
				"resources/icons/customized/BDD4.png");
		setDefaultPageImageDescriptor(imageDesc);	
		
	}
	
	/**
	 * If a user select the finish button, then open BDD's configurator.
	 * 
	 * @param monitor
	 */
	private void doFinish(IProgressMonitor monitor)
	{
		// Get selected project information from the selection page.
		//
		activeProject = selectionPage.getSelectedProject();
		
		IWorkbenchPage  wbPage = workbench.getActiveWorkbenchWindow().getActivePage();
		String	editorId = "cs.vt.edu.bdd.editors.BDDConfig";
		
		String fileName = activeProject.getName() + ".bdd";
		
		
		IFile file = activeProject.getFile(fileName);
		
		// 
		if( !activeProject.exists(file.getFullPath()))
		{
//			activeProject.getF
//			file.create(is, true, monitor);
		}
				
		try{
			IDE.openEditor(wbPage, file, editorId);
		}
		catch (PartInitException e) {
			
		}
	}
}
