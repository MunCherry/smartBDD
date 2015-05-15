package cs.vt.edu.bdd.editors;

import java.awt.Frame;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.junit.runner.notification.Failure;

import cs.vt.edu.bdd.editors.ScenarioSelectionPage.ScenarioTreeContentProvider;
import cs.vt.edu.bdd.editors.ScenarioSelectionPage.ScenarioTreeLabelProvider;
import cs.vt.edu.bdd.editors.ScenarioSelectionPage.ScenarioTreeSelectionChangedListener;

public class ReportPage {
	
	private BDDConfig		part = null;
	private Composite				container = null;
	private String					title;
	private String					id;
	private List<ScenarioResult> scenarioResultList = null;
	
	private ScrolledForm 			scrolledForm;
	
	private Tree resultsTree;
	private TreeViewer resultsTreeViewer;
	
	private ScenarioResultTreeNode root = null;
	private ScenarioResultTreeNode successRoot = null;
	private ScenarioResultTreeNode failureRoot = null;
	
	private ScenarioResultTreeNode selectedScenarioTreeNode;
	
	private Text rawFailureText = null; 
	
	public ReportPage(BDDConfig part, String id, String title, Composite container, List<ScenarioResult> result) {
		this.part = part;
		this.container = container;
		this.title = title;
		this.id = id;
		this.scenarioResultList = result;
	
		createFormContent(this.container);
		
		initializePage();
		
		loadContents();

	}
	
	public void createPage(){
		createFormContent(container);	
		
		initializePage();
		
		loadContents();
		
	}
	
	public BDDConfig getPart()
	{
		return this.part;
	}
	
	/**
	 * 
	 * @param container
	 */
	protected void createFormContent(Composite container) {
		
		FormToolkit toolkit = new FormToolkit(container.getDisplay());
		scrolledForm = toolkit.createScrolledForm(container);
		scrolledForm.setText(this.title);

		scrolledForm.setExpandVertical(true);
		scrolledForm.setExpandHorizontal(true);
		scrolledForm.setMinSize(1000, 1000);

		Composite body = scrolledForm.getBody();
		body.setLayout(new GridLayout(2, false));

		toolkit.decorateFormHeading(scrolledForm.getForm());
		toolkit.paintBordersFor(body);

		/*
		 * Create a section for tree viewer. A user can select a specification file on the list.
		 */
		Section treeSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		toolkit.paintBordersFor(treeSection);
		treeSection.setText("You have run the following scenarios");
		treeSection.setExpanded(true);
		treeSection.setLayout(new GridLayout());

		GridData treeSectionData = new GridData(GridData.FILL_VERTICAL);
		treeSectionData.verticalSpan=1;
		treeSection.setLayoutData(treeSectionData);

		createTreeSectionContent(toolkit, treeSection);
			
		Section infoSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		infoSection.setText("Detail Running Information");
		infoSection.setExpanded(true);
		infoSection.setLayout(new GridLayout());
		
		infoSection.setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		
		createInfoSectionContents(toolkit, infoSection);		
		
		Section navigationSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		navigationSection.setText("Navigation");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		navigationSection.setLayoutData(gridData);
		navigationSection.setLayout(new GridLayout());
		
		
		
		createNaviagationSectionContents(toolkit, navigationSection);

	}
	
	private void createInfoSectionContents(FormToolkit toolkit,
			Section parent) {
			
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);
		
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessVerticalSpace = true;
		sectionComp.setLayoutData(layoutData);	
		
		GridLayout layout = new GridLayout();		
		layout.marginWidth = 0;
		layout.marginHeight = 0;		
		layout.numColumns = 3;
		sectionComp.setLayout(layout);
		
		toolkit.paintBordersFor(sectionComp);	
		
		rawFailureText = new Text(sectionComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 200;
		rawFailureText.setLayoutData(gridData);
		rawFailureText.setEditable(false);
	}

	private void createNaviagationSectionContents(FormToolkit toolkit, Section parent)
	{
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);
		
		GridData layoutData = new GridData(GridData.FILL_BOTH);		
		layoutData.grabExcessVerticalSpace = true;
		sectionComp.setLayoutData(layoutData);
		
		toolkit.paintBordersFor(sectionComp);
		
		GridLayout layout = new GridLayout();		
		layout.marginWidth = 10;
		layout.marginHeight = 10;		
		layout.numColumns = 3;
		sectionComp.setLayout(layout);
		
		GridData navGridData = new GridData();
		navGridData.heightHint = 150;
		navGridData.horizontalIndent = 5;
		sectionComp.setLayoutData(navGridData);
		
		Button backButton = new Button(sectionComp, SWT.PUSH);		
		backButton.setText("Back to Main");
		backButton.setSize(150, 150);
		
		Button saveButton = new Button(sectionComp, SWT.PUSH);		
		saveButton.setText("Save Results");
		saveButton.setSize(150, 150);
		
		Button okButton = new Button(sectionComp, SWT.PUSH);		
		okButton.setText("Close");
		okButton.setSize(100, 100);
		
		Listener listener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				part.removePage(1);
			}
		};
		
		okButton.addListener(SWT.Selection, listener);
		
	}
	
	private void createTreeSectionContent(FormToolkit toolkit, Section parent)
	{
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);
		
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 1500;
		
		sectionComp.setLayoutData(layoutData);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;		
		sectionComp.setLayout(layout);		
		
		createScenariolTreeViewer(toolkit, sectionComp);		 		
	}
	
	private void createScenariolTreeViewer(FormToolkit toolkit, Composite parent)
	{
		Composite treeComp = toolkit.createComposite(parent);
		treeComp.setLayout(new GridLayout());
		treeComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		resultsTree = toolkit.createTree(treeComp, 
				SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
	
		String[] colItemName = {"Scenario", "State"};
		int[] colAlign = {SWT.CENTER, SWT.CENTER};
		int[] colWidth = {50, 50};
		
		for(int i=0; i<colItemName.length; i++) {
			TreeColumn colItem = new TreeColumn(resultsTree, colAlign[i]);
			colItem.setResizable(true);
			colItem.setText(colItemName[i]);
			colItem.setWidth(colWidth[i]);
		}
				
		TableLayout columnLayout = new TableLayout();
		columnLayout.addColumnData(new ColumnWeightData(50, true));
		columnLayout.addColumnData(new ColumnWeightData(50, true));
		
		resultsTree.setLinesVisible(true);
		resultsTree.setHeaderVisible(true);
		resultsTree.setLayout(columnLayout);
		
		GridData treeGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		treeGridData.horizontalAlignment = GridData.FILL;
		treeGridData.verticalAlignment = GridData.FILL;
		treeGridData.grabExcessHorizontalSpace = true;
		treeGridData.grabExcessVerticalSpace = true;	
		treeGridData.widthHint = 400;
		treeGridData.heightHint = 150;
		resultsTree.setLayoutData(treeGridData);
		
		resultsTreeViewer = new TreeViewer(resultsTree);
				
		/**
		 * Add providers to feature tree viewer.
		 */
		resultsTreeViewer.setContentProvider(new ResultTreeContentProvider());
		resultsTreeViewer.setLabelProvider(new ResultTreeLabelProvider());
		

		/**
		 * Add tree item selection listener.
		 */
		resultsTreeViewer.addSelectionChangedListener(
				new ResultTreeSelectionChangedListener() {
					
				});
	}
	
	
	public class ResultTreeSelectionChangedListener implements ISelectionChangedListener
	{

		public void selectionChanged(SelectionChangedEvent event) {
		
			IStructuredSelection selection = 
				(IStructuredSelection)event.getSelection();
			
			if(selection == null)
				return;
			
			if(selection.isEmpty())
				return;
					
			selectedScenarioTreeNode = (ScenarioResultTreeNode)selection.getFirstElement();
			
			String detailedOutput = null;
			if (selectedScenarioTreeNode.getStatus() == "Success") {
				rawFailureText.setBackground(new Color(Display.getCurrent(), 0, 255, 25));
				detailedOutput = selectedScenarioTreeNode.getSuccessText();
				
			}
			else if (selectedScenarioTreeNode.getStatus() == "Failure") {
				rawFailureText.setBackground(new Color(Display.getCurrent(), 255, 100, 0));
				detailedOutput = selectedScenarioTreeNode.getFailureText();
			}	
			else {
				rawFailureText.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
				detailedOutput = "";				
			}
			
			rawFailureText.setText(detailedOutput);
			
		}
		
	}
	
	
	public class ResultTreeContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			if(inputElement == null)
				return null;
			
			ScenarioResultTreeNode node = (ScenarioResultTreeNode)inputElement;
			
			return node.getChildren().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			
			if(parentElement == null)
				return null;
			
			ScenarioResultTreeNode node = (ScenarioResultTreeNode)parentElement;
			
			return node.getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			if(element == null)
				return null;
			
			ScenarioResultTreeNode node = (ScenarioResultTreeNode)element;
			
			return node.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			if(element == null)
				return false;
			
			ScenarioResultTreeNode node = (ScenarioResultTreeNode)element;
			
			if(node.getChildren().size() > 0)
				return true;
			else
				return false;

		}
		
	}
	
	
	public class ResultTreeLabelProvider implements ITableLabelProvider{

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			if(element instanceof ScenarioResultTreeNode) 
			{
				ScenarioResultTreeNode item = (ScenarioResultTreeNode)element;
				
				switch(columnIndex) {
				case 0:
					return item.getName();
				case 1:
					return item.getStatus();
				default:
					return null;
				}
			}
			return null;

		}
		
	}
	
	private void initializePage()
	{
		root = new ScenarioResultTreeNode("Test Status");
		successRoot = new ScenarioResultTreeNode("Success");
		failureRoot = new ScenarioResultTreeNode("Failure");
		
		root.addChild(successRoot);
		root.addChild(failureRoot);
	}
	
	/**
	 * Load resources to tree viewer.
	 */
	private void loadContents()
	{
		
		try{
			//Add the data from the scenario result to the table.
			for(ScenarioResult result: scenarioResultList) {
				
				if(result.isSuccess()) {
					ScenarioResultTreeNode item = new ScenarioResultTreeNode(result.getScenarioTitle(), null, "Success");
					successRoot.addChild(item);
				}
				else  {
					ScenarioResultTreeNode item = new ScenarioResultTreeNode(result.getScenarioTitle(), result.getFailure(), "Failure");
					failureRoot.addChild(item);
				}
				
			}
			/*{
							ScenarioTreeNode item = new ScenarioTreeNode(file);
									scenarioRoot.addChild(item);
								}
								else if(extension.equals(FileExtension.FEATURE_EXT))
								{
									ScenarioTreeNode item = new ScenarioTreeNode(file);
									featureRoot.addChild(item);					
								}                        
								else if (extension.equals(FileExtension.DOC_EXT.doc.toString()) || 
										extension.equals(FileExtension.DOC_EXT.docx.toString()) ||
										extension.equals(FileExtension.DOC_EXT.txt.toString())){
									ScenarioTreeNode item = new ScenarioTreeNode(file);
									docRoot.addChild(item);						
								}
							}
						}
						
						break;
						
					}										
				}
			}*/
			
		} catch (Exception e1) {
			
		}		
		
		resultsTreeViewer.setInput(root);
		resultsTreeViewer.expandAll();
	}


	
	public Composite getForm()
	{
		return scrolledForm;
	}

}
