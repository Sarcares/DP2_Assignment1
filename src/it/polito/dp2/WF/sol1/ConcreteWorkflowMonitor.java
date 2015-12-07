package it.polito.dp2.WF.sol1;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.polito.dp2.WF.Actor;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowReader;
import it.polito.dp2.WF.util.WFAttributes;
import it.polito.dp2.WF.util.WFElements;

/**
 * This is a concrete implementation of the interface WorkflowMonitor.<BR><BR>
 * If you want more detail about the interface look to {@link it.polito.dp2.WF.WorkflowMonitor}
 * 
 * @author Luca
 */
public class ConcreteWorkflowMonitor implements it.polito.dp2.WF.WorkflowMonitor {

	private HashMap<String, ProcessReader> processes;
	private HashMap<String, WorkflowReader> workflows;
	private HashMap<String, Actor> actors;

	//TODO: devo considerare che si possa creare un WorkflowManager vuoto?!
	public ConcreteWorkflowMonitor() {/*default constructor*/
		processes = new HashMap<String, ProcessReader>();
		workflows = new HashMap<String, WorkflowReader>();
		actors = new HashMap<String, Actor>();
	}
	//TODO: nell'implementazione di Sisto viene tutto generato nel costruttore di default 
	
	//if I valid the document before creating the element I can assume some stuff
	public ConcreteWorkflowMonitor(Set<ProcessReader> processes, Set<WorkflowReader> workflows, Set<Actor> actors)
																	throws IllegalArgumentException {
		if( (processes == null) || (processes.size() == 0) )
    		throw new IllegalArgumentException("Wrong parameter, \"processes\" was null or empty!");
		
		if( (workflows == null) || (workflows.size() == 0) )
			throw new IllegalArgumentException("Wrong parameter, \"workflows\" was null or empty!");
		
		this.processes = new HashMap<String, ProcessReader>();
		int code = 1;	//TODO: maybe could be something wrong on this use of code...
		for(ProcessReader proc : processes) {
			this.processes.put("p"+code, proc);
			code++;
		}
		this.workflows = new HashMap<String, WorkflowReader>();
		for(WorkflowReader wfr : workflows) {
			this.workflows.put(wfr.getName(), wfr);
		}
		this.actors = new HashMap<String, Actor>();
		for(Actor a : actors) {
			this.actors.put(a.getName(), a);
		}
	}
	
	public ConcreteWorkflowMonitor(Element element) {
		setParameter(element);
	}

	@Override
	public Set<ProcessReader> getProcesses() {
		return new TreeSet<ProcessReader>(processes.values());
	}

	@Override
	public WorkflowReader getWorkflow(String name) {
		return workflows.get(name);
	}

	@Override
	public Set<WorkflowReader> getWorkflows() {	//TODO: test this method
		return new TreeSet<WorkflowReader>(workflows.values());
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer("Inside this WorkflowMonitor there are:\n");
		
		if(workflows==null)
			buf.append("\tNo Workflows\n");
		else {
			buf.append("\tWorkflows:\n");
			for(WorkflowReader wfr : workflows.values())
				buf.append("\t\t"+wfr.toString()+"\n");
		}
		
		if(processes==null)
			buf.append("\tNo Processes\n");
		else {
			buf.append("\tProcesses:\n");
			for(ProcessReader pr : processes.values())
				buf.append("\t\t"+pr.toString()+"\n");
		}
		return buf.toString();
	}
	
	public Actor getActor(String name) {
		return actors.get(name);
	}
	
	public Set<Actor> getActors() {
		return new TreeSet<Actor>(actors.values());
	}
	
	public void setParameter(Element root) {
		if (root == null)
    		throw new IllegalArgumentException("Wrong parameter, element was null!");
    	
		workflows = new HashMap<String, WorkflowReader>();
		processes = new HashMap<String, ProcessReader>();
		actors = new HashMap<String, Actor>();
		int i=0;
		NodeList wfNodes = root.getElementsByTagName(WFElements.WORKFLOW);		//"workflow"
		NodeList procNodes = root.getElementsByTagName(WFElements.PROCESS);		//"process"
		
		/* workflows */
		System.out.println("DEBUG - In the document there are "+wfNodes.getLength()+" workflows");
	    for (i=0; i<wfNodes.getLength(); i++) {
	    	if(wfNodes.item(i) instanceof Element) {	//if I don't take an element I ignore it
	    		WorkflowReader wf = new ConcreteWorkflowReader((Element)wfNodes.item(i), procNodes);
	    		workflows.put(wf.getName(), wf);
	    	}
	    }
		System.out.println("DEBUG - Workflows created");
		
		/* processes */
		System.out.println("DEBUG - In the document there are "+procNodes.getLength()+" processes");
		int code = 1;
		for (i=0; i<procNodes.getLength(); i++) {
			if(procNodes.item(i) instanceof Element) {	//if I don't take an element I ignore it
				Element e = (Element) procNodes.item(i);
				//I should have already the workflow inside the hashmap (document should be valid)
				WorkflowReader myWF = workflows.get(e.getAttribute(WFElements.WORKFLOW));
				System.out.println("DEBUG - My workflow is: "+myWF.getName());
				
		    	ProcessReader proc = new ConcreteProcessReader(e, myWF);		    	
		    	processes.put("p"+code, proc);
		    	code++;
			}
	    }
		System.out.println("DEBUG - Processes created");
		
		/* actors */	//TODO: update this part if you want to manage more departments		
		NodeList actorsNodes = root.getElementsByTagName( WFElements.ACTORS );		//"actors"
		System.out.println("DEBUG - Number of tag actors: "+actorsNodes.getLength());
		// this loop is executed just one time in this particular application
		for(i=0; i<actorsNodes.getLength(); i++) {
			if(actorsNodes.item(i) instanceof Element) {	//if I don't take an element I ignore it
				Element e = (Element) actorsNodes.item(i);
				NodeList acts = e.getElementsByTagName( WFElements.ACTOR );			//"actor"
				System.out.println("DEBUG - Number of actor: "+acts.getLength());
				for(int j=0; j<acts.getLength(); j++) {
					if(acts.item(i) instanceof Element) {	//if I don't take an element I ignore it
						e = (Element) acts.item(j);
						String name = e.getAttribute( WFAttributes.ACTOR_NAME );
						String role = e.getAttribute( WFAttributes.ACTOR_ROLE );
						
						Actor a = new Actor(name, role);						
						actors.put(a.getName(), a);
					}
				}
			}
		}
		System.out.println("DEBUG - Actors created");
		
		return;
	}

}
