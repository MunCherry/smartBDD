package cs.vt.edu.bdd.wizard;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 *  
 * @author Tung
 *
 */
public class ProjectSelectionPage extends WizardPage {

	private IStructuredSelection selection;
	
	private Text			projectName;
	private IProject		selectedProject;
	
	public ProjectSelectionPage(String title, IWorkbench workbench, IStructuredSelection selection)
	{
		super(title);
		this.selection = selection;

	}
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL |
				GridData.HORIZONTAL_ALIGN_FILL));
		composite.setFont(parent.getFont());
		
		createProjectSelectionControl(composite);
		
		setPageComplete(true);
		setControl(composite);
	}
	
	/**
	 * 
	 * 
	 * @param parent
	 */
	private void createProjectSelectionControl(Composite parent)
	{
		// Create Group Control for project selection.
		
		Composite projectNameGroup = new Composite(parent, SWT.None);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		projectNameGroup.setLayout(layout);
		projectNameGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL |
				GridData.GRAB_HORIZONTAL));
		projectNameGroup.setFont(parent.getFont());
		
		// Create Project Selection Control
	
		GridData data = new GridData(GridData.GRAB_HORIZONTAL);
		data.widthHint = 130;
		
		Label label = new Label(projectNameGroup, SWT.None);
		label.setText("Project: ");
		label.setFont(parent.getFont());
		label.setLayoutData(data);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 250;
		projectName = new Text(projectNameGroup, SWT.BORDER | SWT.READ_ONLY);
		projectName.setLayoutData(data);
		projectName.setFont(parent.getFont());
		
		// Set default selected project
	
		setDefaultProjectData();

		projectName.addModifyListener(new ModifyListener(){
			//@Override
			public void modifyText(ModifyEvent e) 
			{	
				if(checkProjectSelection()) 
				{
					setPageComplete(true);
				}
				else 
				{
					setPageComplete(false);
				}
			}		
		});
		
		Button browserBtn = new Button(projectNameGroup, SWT.BORDER);
		browserBtn.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		browserBtn.setFont(parent.getFont());
		browserBtn.setText("Browser");

		// Add listener to project browser button
		//
		browserBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				IProject project = openProjectListDialog();
				
				if(project == null)
					return;
				
				if(selectedProject == null)
					return;
				
				setProjectName(project.getName());
				
			}
		});
		
	}
	
		
	/**
	 *  
	 */
	private IProject openProjectListDialog()
	{
		ILabelProvider labelProvider = new LabelProvider();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(),labelProvider);
		
		dialog.setTitle("Select a Project");
		dialog.setMessage("A Java project");
		dialog.setElements(getAllProject());
		
		if (projectName.getText().length() > 0) 
		{
			IProject project = getWorkspaceRoot().getProject(projectName.getText());
			
			if (isValidProject(project)) 
			{
				dialog.setInitialSelections(new Object[] { project });
			}
		}
		
		if (dialog.open() == Window.OK) {
			selectedProject = (IProject) dialog.getFirstResult();
			return (IProject) dialog.getFirstResult();
			
		}		
		return null;		
	}
	
	/**
	 *  
	 * @return
	 */
	private IProject[] getAllProject()
	{
		return getWorkspaceRoot().getProjects();
	}
	
	/**
	 * 
	 */
	private void setDefaultProjectData()
	{
		Object resource = selection.getFirstElement();
		
		if(resource == null)
			return;
		
		if(resource instanceof IProject)
		{
			setProjectName(((IProject)resource).getName());
			selectedProject = (IProject)resource;
		}
		else if(resource instanceof IFolder)
		{
			IFolder folder = (IFolder)resource;
			String name =  folder.getProject().getName();
			setProjectName(name);
			selectedProject = folder.getProject();
		}
		else if(resource instanceof IFile)
		{
			IFile file = (IFile)resource;
			String name = file.getProject().getName();
			setProjectName(name);
			selectedProject = file.getProject();
		}
		else
			return;
	}
	
	/**
	 *  
	 * @return
	 */
	private boolean checkProjectSelection()
	{
		 String name = projectName.getText();
		 
		if(name.length() == 0)
		{
			setErrorMessage("프로젝트를 선택해 주세요.");
			return false;
		}
		setErrorMessage(null);
		return true;		
	}
	
	/**
	 * projectName 
	 * @param name
	 */
	public void setProjectName(String name)
	{
		projectName.setText(name);
	}
	
	public IProject getSelectedProject()
	{
		return selectedProject;
	}

	/**
	 * 
	 * @return
	 */
	private IWorkspaceRoot getWorkspaceRoot()
	{
		return ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 *  
	 * @param project
	 * @return
	 */
	private boolean isValidProject(IProject project)
	{
		return project != null && project.exists() && project.isOpen();
	}
}


