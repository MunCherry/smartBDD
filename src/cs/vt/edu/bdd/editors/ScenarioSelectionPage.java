package cs.vt.edu.bdd.editors;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import cs.vt.edu.bdd.generator.AbstractCodeGenerator;
import cs.vt.edu.bdd.generator.JunitTestCodeConstants;
import cs.vt.edu.bdd.generator.JunitTestCodeGenerator;
import cs.vt.edu.bdd.matcher.MethodMatchingHelper;
import cs.vt.edu.bdd.nlp.Analyzer;
import cs.vt.edu.bdd.nlp.Clause;
import cs.vt.edu.bdd.nlp.IO;
import cs.vt.edu.bdd.nlp.Scenario;
import cs.vt.edu.bdd.nlp.StringHelper;
import cs.vt.edu.bdd.nlp.SVO;
import cs.vt.edu.bdd.reflection.JavaClassDetailsImpl;
import cs.vt.edu.bdd.utility.BDDUtility;
import edu.stanford.nlp.io.StringOutputStream;

public class ScenarioSelectionPage {

	private BDDConfig config;

	private ScenarioTreeNode root;
	private ScenarioTreeNode scenarioRoot;
	private ScenarioTreeNode featureRoot;
	private ScenarioTreeNode docRoot;

	private Tree scenarioTree;
	private TreeViewer scenarioTreeViewer;
	private ScenarioTreeNode selectedScenarioTreeNode;

	private Composite container;
	private String title;
	private String id;
	private String selectedProjectPath;

	private ScrolledForm scrolledForm;

	private Text rawScenarioText;
	/*
	private Button analyzeButton;
	private Button posButton;
	private Button svoButton;
	*/
	private Button generateCodeButton;
	
	private Text generatedCodeText;
	private Text parsedScenarioText;

	private Button testButton;

	private Button tempButton;


	private Analyzer analyzer;

	private SVO svo;
	
	List<Scenario> resultList = null;

	public ScenarioSelectionPage(BDDConfig conf, String id, String title, Composite container) {
		this.config = conf;
		this.container = container;
		this.title = title;
		this.id = id;
		this.selectedProjectPath=config.getActiveProject().getLocation().toOSString();

		analyzer = new Analyzer();		

		createFormContent(this.container);


		initializePage();

		loadContents();
	}

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
		treeSection.setText("1. Select Scenarios to Test");
		treeSection.setExpanded(true);
		treeSection.setLayout(new GridLayout());

		GridData treeSectionData = new GridData(GridData.FILL_VERTICAL);
		treeSectionData.verticalSpan=2;
		treeSection.setLayoutData(treeSectionData);

		createTreeSectionContent(toolkit, treeSection);


		Section rawScenarioContentSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		rawScenarioContentSection.setText("2. Scenario Text Content");
		rawScenarioContentSection.setExpanded(true);		
		rawScenarioContentSection.setLayout(new GridLayout());

		GridData rawScenarioGridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		rawScenarioContentSection.setLayoutData(rawScenarioGridData);		

		createScenarioContentSection(toolkit, rawScenarioContentSection);


		Section parsedScenarioSection = toolkit.createSection(body, ExpandableComposite.TITLE_BAR);
		parsedScenarioSection.setText("3. Analyzed Scenario Content");
		parsedScenarioSection.setLayoutData(
				new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		parsedScenarioSection.setLayout(new GridLayout());

		createParsedSenarioSectionContents(toolkit, parsedScenarioSection);

	}


	private void createParsedSenarioSectionContents(FormToolkit toolkit, Section parent)
	{
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);

		GridData layoutData = new GridData(GridData.FILL_BOTH);		
		layoutData.grabExcessVerticalSpace = true;
		sectionComp.setLayoutData(layoutData);

		toolkit.paintBordersFor(sectionComp);

		GridLayout layout = new GridLayout();		
		layout.marginWidth = 0;
		layout.marginHeight = 0;		
		sectionComp.setLayout(layout);	
		
		generatedCodeText = new Text(sectionComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.heightHint = 250;
		generatedCodeText.setLayoutData(gridData);
		generatedCodeText.setEditable(true);
		
		testButton = new Button(sectionComp, SWT.PUSH);		
		testButton.setText("Run generated code");
		testButton.setSize(100, 100);

		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				
				String testOutput = null;
				List<ScenarioResult> scenarioResultList = new ArrayList<ScenarioResult>();
				
				try {
					
					File file = new File(selectedProjectPath + 
							System.getProperty("file.separator") + JavaClassDetailsImpl.CLASS_FILES_DIRECTORY);
					
					String selectedFilePath = selectedScenarioTreeNode.getFile().getLocation().toOSString();
					
					String pathOfClass = JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE + "." + BDDUtility.extractClassName(selectedFilePath) + 
				  				JunitTestCodeConstants.TEST_EXTENTION;
					
					System.out.println(file.getAbsolutePath());
					System.out.println(selectedFilePath);
					System.out.println(pathOfClass);
					
					//URL url = file.toURI().toURL();
					//System.out.println(url.toString());
					//URL[] urlList = new URL[]{url};
					URLClassLoader loader = new URLClassLoader(new URL[]
							{file.toURI().toURL()}, JUnitCore.class.getClassLoader());			
					
					Class<?> operatingClass = Class.forName(pathOfClass, true, loader);
					
					System.out.println(operatingClass.getName());
					
					OutputStream oStream = new StringOutputStream();
					PrintStream printStream = new PrintStream(oStream);
					
					//Here we would need to run the Junit test code and capture the results.
					JUnitCore core = new JUnitCore();
					core.addListener(new TextListener(printStream));
					Result result = core.run(operatingClass);

					//Capture the output and display it in the result panel
					testOutput = printStream.toString();
					System.out.println(testOutput);
				
					System.out.println(resultList);
					
					for(Scenario scenario: resultList) {
						
						if(scenario != null) 
						{
							//Create new scenario result
							String scenarioTitle = scenario.geTitle();
							System.out.println(scenarioTitle);
							Failure currentFailure = null;
							if(scenarioTitle != null) {
								String scenarioTitleWithoutSpaces = "test" + scenarioTitle.replace(" ", "").substring(scenarioTitle.lastIndexOf(":"));
								System.out.println("No Spaces: " + scenarioTitleWithoutSpaces);
								for(Failure failure: result.getFailures()) {
									System.out.println("Failure: " + failure.getTestHeader().toLowerCase());
									if((failure.getTestHeader().toLowerCase().contains(scenarioTitleWithoutSpaces.toLowerCase() + "("))) {
										System.out.println("Match!!!");
										currentFailure = failure;
										break;
									}
								}
							}
							else {
								scenarioTitle = "No scenario title";
							}
							
							ScenarioResult scenarioResult = new ScenarioResult();
							if(currentFailure == null) {
								scenarioResult.setSuccess(true);
							}
							scenarioResult.setFailure(currentFailure);
							scenarioResult.setScenarioTitle(scenarioTitle);
							scenarioResultList.add(scenarioResult);
							System.out.println(scenarioResult.toString());
						}
					}
					
					//for(Failure failure :result.getFailures()) {
					//	System.out.println("Failure for: " + failure.getTestHeader());
					//}
					
				}
				catch(Exception ex) {
					MessageDialog.openError(null, "BDD", "An error occured: " + ex.getMessage());
					ex.printStackTrace();
				}
				
				//We can send the result output to the result page
				config.createPage1(scenarioResultList);
				
			}
		};

		testButton.addListener(SWT.Selection, listener);

	}


	/**
	 * Create controls for options.
	 * @param toolkit
	 * @param parent
	 */
	private void createScenarioContentSection(FormToolkit toolkit, Section parent)
	{
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);

		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessVerticalSpace = true;
		sectionComp.setLayoutData(layoutData);	

		GridLayout layout = new GridLayout();		
		layout.marginWidth = 0;
		layout.marginHeight = 0;		
		layout.numColumns = 4;
		sectionComp.setLayout(layout);

		toolkit.paintBordersFor(sectionComp);	

		rawScenarioText = new Text(sectionComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 4;
		gridData.heightHint = 200;
		rawScenarioText.setLayoutData(gridData);
		rawScenarioText.setEditable(true);
		
		generateCodeButton = new Button(sectionComp, SWT.PUSH);
		generateCodeButton.setText("Generate Test Code");
		generateCodeButton.setSize(100, 100);
		
		Listener parseListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
								
				if (rawScenarioText != null && rawScenarioText.getText() != "") {
					
					/*
					String inputText = rawScenarioText.getText().toString();
					//Scenario scenario = analyzer.TextToScenario(inputText);
					String temp = "";					
					listOfScenarios = analyzer.TextsToScenarios(inputText); 
					for (int i=0; i<listOfScenarios.size(); i++){
						temp += listOfScenarios.get(i).toPlainTextWithPOSTags() + "\n\n\n";
					}
					*/
					
					StringBuffer generatedFileContents = null;	
					try {
						//Get the current project folder and the selected scenario folder.
						String selectedFilePath = selectedScenarioTreeNode.getFile().getLocation().toOSString();
						System.out.println(selectedProjectPath);
						System.out.println(selectedFilePath);
						
						AbstractCodeGenerator generator = new JunitTestCodeGenerator(selectedProjectPath);
						//generator.generateTestCode("/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test", "/Users/sunilkamalakar/Desktop/MittelmanLab/workspace-se/BDD-test/bdd-test/Stack.feature");
						resultList = ((JunitTestCodeGenerator)generator).obtainScenarios(selectedFilePath);
						generator.generateTestCode(selectedFilePath);
						
						String pathToFile = selectedProjectPath + System.getProperty("file.separator") + JunitTestCodeConstants.PROJECT_BASE_SOURCE +
								System.getProperty("file.separator") + JunitTestCodeConstants.JUNIT_TEST_CLASS_PACKAGE.replace(".", System.getProperty("file.separator"))
					  			+ System.getProperty("file.separator") + BDDUtility.extractClassName(selectedFilePath) + 
					  				JunitTestCodeConstants.TEST_EXTENTION + ".java";
						
						File generatedFile = new File(pathToFile);
					
						generatedFileContents = new StringBuffer(FileUtils.readFileToString(generatedFile));
					}
					catch (Exception ex) {
						MessageDialog.openError(null, "BDD", "An error occured: " + ex.getMessage());
						ex.printStackTrace();
					}
					
					if(generatedFileContents != null)
						generatedCodeText.setText(generatedFileContents.toString());
						
				} else {
					MessageDialog.openError(null, "BDD", "Please select a scenario file");
				}
				
			}
		};
		
		generateCodeButton.addListener(SWT.Selection, parseListener);
		
		/*
		analyzeButton = new Button(sectionComp, SWT.PUSH);		
		analyzeButton.setText("Parse Scenarios");
		analyzeButton.setSize(100, 100);

		Listener parseListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub

				if (rawScenarioText != null && rawScenarioText.getText() != "") {
					String inputText = rawScenarioText.getText().toString();
					//Scenario scenario = analyzer.TextToScenario(inputText);
					String temp = "";					
					listOfScenarios = analyzer.TextsToScenarios(inputText); 
					for (int i=0; i<listOfScenarios.size(); i++){
						temp += listOfScenarios.get(i).toPlainTextWithPOSTags() + "\n\n\n";
					}
					parsedScenarioText.setText(temp);
				} else {
					MessageDialog.openError(null, "BDD", "Please select a scenario file");
				}

			}
		};

		analyzeButton.addListener(SWT.Selection, parseListener);

		// for playing with POS: searching nouns and verbs that 
		// candidates of class and methods

		posButton = new Button(sectionComp, SWT.PUSH);		
		posButton.setText("Find Verbs");		


		Listener posListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub

				String whenClausesVerb = "";

				if (listOfScenarios != null) {
					for (int i=0; i<listOfScenarios.size(); i++){
						for (Clause clause : listOfScenarios.get(i).getGivenContent().getClauses()){
							whenClausesVerb += StringHelper.TokensListToString(clause.GetVerbs()) + "\n\n";
						}
					}
				}

				parsedScenarioText.setText(whenClausesVerb);
			}
		};

		posButton.addListener(SWT.Selection, posListener);

		// button for testing stanford Subject-verb-object
		svoButton = new Button(sectionComp, SWT.PUSH);
		svoButton.setText("SVO analyze");

		Listener svoListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (svo == null) {
					svo = new SVO();
				}
				String text = rawScenarioText.getText();
				String results = svo.test(text);
				parsedScenarioText.setText(results);		
			}
		};

		svoButton.addListener(SWT.Selection, svoListener);

		// temporary test button
		tempButton = new Button(sectionComp, SWT.PUSH);
		tempButton.setText("Test");
		Listener tempListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				String text = rawScenarioText.getText();
				//StringHelper helper = new StringHelper();				
				String[] words = StringHelper.seperateWordsWithWhitespace(text);
				boolean found = StringHelper.isMatched(words[0], words[1], Integer.parseInt(words[2]));
				if (found) {
					parsedScenarioText.setText("Found");
				} else{
					parsedScenarioText.setText("Not Found");
				}
				//				String result="";
				//				for (int i=0; i<words.length; i++){
				//					result += words[i] + "\n";
				//				}
				//				parsedScenarioText.setText(result);

			}
		};
		tempButton.addListener(SWT.Selection, tempListener);

		*/
	}

	private void createTreeSectionContent(FormToolkit toolkit, Section parent)
	{
		Composite sectionComp = toolkit.createComposite(parent);
		parent.setClient(sectionComp);

		GridData layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.heightHint = 300;

		sectionComp.setLayoutData(layoutData);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;		
		sectionComp.setLayout(layout);		

		createScenariolTreeViewer(toolkit, sectionComp);		 		
	}


	private void initializePage()
	{
		root = new ScenarioTreeNode("Project Structure");
		scenarioRoot = new ScenarioTreeNode("Scenario");
		featureRoot = new ScenarioTreeNode("Feature");
		docRoot = new ScenarioTreeNode("Document");

		root.addChild(scenarioRoot);
		root.addChild(featureRoot);
		root.addChild(docRoot);
	}

	/**
	 * Load resources to tree viewer.
	 */
	private void loadContents()
	{
		IProject project = config.getActiveProject();

		try{
			IResource[] members = project.members();

			for(int i=0; i<members.length; i++)
			{
				if (members[i] instanceof IFolder){
					IFolder folder = (IFolder) members[i];
					String folderName = folder.getName();
					if (folderName.toLowerCase().endsWith("scenarios")){

						IResource[] folderMembers = folder.members();

						for (int j=0; j<folderMembers.length; j++){

							if(folderMembers[j] instanceof IFile) {

								IFile file = (IFile)folderMembers[j];

								String extension = file.getFileExtension().toLowerCase();

								if(extension.equals(FileExtension.SCENARIO_EXT))
								{
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
			}

		} catch (CoreException e1) {

		}		

		scenarioTreeViewer.setInput(root);
		scenarioTreeViewer.expandAll();
	}

	private void createScenariolTreeViewer(FormToolkit toolkit, Composite parent)
	{
		Composite treeComp = toolkit.createComposite(parent);
		treeComp.setLayout(new GridLayout());
		treeComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		scenarioTree = toolkit.createTree(treeComp, 
				SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

		String[] colItemName = {"Story", "Type" /*, "Select"*/};
		int[] colAlign = {SWT.LEFT, SWT.CENTER};
		int[] colWidth = {300, 90};

		for(int i=0; i<colItemName.length; i++) {
			TreeColumn colItem = new TreeColumn(scenarioTree, colAlign[i]);
			colItem.setResizable(true);
			colItem.setText(colItemName[i]);
			colItem.setWidth(colWidth[i]);
		}

		TableLayout columnLayout = new TableLayout();
		columnLayout.addColumnData(new ColumnWeightData(80, true));
		columnLayout.addColumnData(new ColumnWeightData(20, true));

		scenarioTree.setLinesVisible(true);
		scenarioTree.setHeaderVisible(true);
		scenarioTree.setLayout(columnLayout);

		GridData treeGridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		treeGridData.horizontalAlignment = GridData.FILL;
		treeGridData.verticalAlignment = GridData.FILL;
		treeGridData.grabExcessHorizontalSpace = true;
		treeGridData.grabExcessVerticalSpace = true;	
		treeGridData.widthHint = 300;
		treeGridData.heightHint = 500;
		scenarioTree.setLayoutData(treeGridData);

		scenarioTreeViewer = new TreeViewer(scenarioTree);

		/**
		 * Add providers to feature tree viewer.
		 */
		scenarioTreeViewer.setContentProvider(new ScenarioTreeContentProvider());
		scenarioTreeViewer.setLabelProvider(new ScenarioTreeLabelProvider());


		/**
		 * Add tree item selection listener.
		 */
		scenarioTreeViewer.addSelectionChangedListener(
				new ScenarioTreeSelectionChangedListener() {

				});
	}

	public class ScenarioTreeSelectionChangedListener implements ISelectionChangedListener
	{

		public void selectionChanged(SelectionChangedEvent event) {

			IStructuredSelection selection = 
					(IStructuredSelection)event.getSelection();

			if(selection == null)
				return;

			if(selection.isEmpty())
				return;			

			selectedScenarioTreeNode = (ScenarioTreeNode)selection.getFirstElement();

			if (selectedScenarioTreeNode.getFile() == null)
				return;

			String selectedFilePath = selectedScenarioTreeNode.getFile().getLocation().toOSString();

			String scenarioText = loadScenarioContent(selectedFilePath);

			rawScenarioText.setText(scenarioText);
		}

	}


	private String loadScenarioContent(String scenarioFilePath){
		return IO.FormatText(IO.readFile(scenarioFilePath));
	}


	public class ScenarioTreeContentProvider implements ITreeContentProvider{

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

			ScenarioTreeNode node = (ScenarioTreeNode)inputElement;

			return node.getChildren().toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub

			if(parentElement == null)
				return null;

			ScenarioTreeNode node = (ScenarioTreeNode)parentElement;

			return node.getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			if(element == null)
				return null;

			ScenarioTreeNode node = (ScenarioTreeNode)element;

			return node.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			if(element == null)
				return false;

			ScenarioTreeNode node = (ScenarioTreeNode)element;

			if(node.getChildren().size() > 0)
				return true;
			else
				return false;

		}

	}


	public class ScenarioTreeLabelProvider implements ITableLabelProvider{

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
			if(element instanceof ScenarioTreeNode) 
			{
				ScenarioTreeNode item = (ScenarioTreeNode)element;

				switch(columnIndex) {
				case 0:
					return item.getFileName();
				case 1:
					return item.getFileExtension();
				default:
					return null;
				}
			}
			return null;

		}

	}

	public Composite getForm()
	{
		return scrolledForm;
	}

}
