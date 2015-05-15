package cs.vt.edu.bdd.editors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;

public class ScenarioTreeNode
{
	private IFile 	file;
	private String  name;
	private boolean isRoot;
	
	private ScenarioTreeNode	parent;
	private List<ScenarioTreeNode> children = new ArrayList<ScenarioTreeNode>();
	
	
	public ScenarioTreeNode(IFile file)
	{
		this.name = file.getName();
		this.file = file;
		this.isRoot = false;
	}
	
	public ScenarioTreeNode(String name)
	{
		this.name = name;
		this.file = null;
		this.isRoot = true;
	}
	
	public ScenarioTreeNode()
	{
		this.name = null;
		this.file = null;
		this.isRoot = true;			
	}
	
	public Object getParent()
	{
		return parent;
	}
	
	public List<ScenarioTreeNode> getChildren()
	{
		return children;
	}
	
	public Object addChild(ScenarioTreeNode child)
	{
		children.add(child);
		child.parent = this;
		
		return this;
	}
	
	public void removeChild(ScenarioTreeNode target)
	{
		children.remove(target);
	}
	
	public IFile getFile()
	{
		return file;
	}
	
	public String getFileName()
	{
		if(isRoot)
			return name;
		else
			return file.getName();
	}
	
	public String getFileExtension()
	{
		if(isRoot)
			return null;
		else
			return file.getFileExtension().toString();
	}
	
	public boolean isRoot()
	{
		return this.isRoot;
	}
}
