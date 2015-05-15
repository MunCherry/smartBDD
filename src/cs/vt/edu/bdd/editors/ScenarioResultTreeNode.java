package cs.vt.edu.bdd.editors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.junit.runner.notification.Failure;

public class ScenarioResultTreeNode
{
	private String  name;
	private String resultStatus;
	private Failure failure = null;
	
	private ScenarioResultTreeNode	parent;
	private List<ScenarioResultTreeNode> children = new ArrayList<ScenarioResultTreeNode>();
	
	
	public ScenarioResultTreeNode(String name, Failure failure, String resultStatus)
	{
		this.name = name;
		this.failure = failure;
		this.resultStatus = resultStatus;
	}
	
	public ScenarioResultTreeNode(String name)
	{
		this.name = name;
		this.failure = null;
		this.resultStatus = "";
	}
	
	public Object getParent()
	{
		return parent;
	}
	
	public List<ScenarioResultTreeNode> getChildren()
	{
		return children;
	}
	
	public Object addChild(ScenarioResultTreeNode child)
	{
		children.add(child);
		child.parent = this;
		
		return this;
	}
	
	public void removeChild(ScenarioResultTreeNode target)
	{
		children.remove(target);
	}
	
	public String getName()
	{
		return name;
	}
		
	public String getStatus()
	{
		return this.resultStatus;
	}
	
	public String getFailureText() {
		
		StringBuffer retVal = new StringBuffer();
		
		retVal.append("Behavior -> " + getName() + "\n" + "has failed" + "\n" + "\n")
			.append(failure.getDescription() + "\n" + "\n")
			.append(failure.getException());
		
		return retVal.toString();	
	}
	
	public String getSuccessText() {
		
		StringBuffer retVal = new StringBuffer();
		retVal.append("Behavior -> " + getName() + "\nhas successfully passed the test");
		return retVal.toString();		
	}

}
