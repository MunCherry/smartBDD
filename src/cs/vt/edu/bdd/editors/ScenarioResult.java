package cs.vt.edu.bdd.editors;

import org.junit.runner.notification.Failure;

public class ScenarioResult {

	private String scenarioTitle = null;

	private boolean success = false;
	private Failure failure = null;

	public String getScenarioTitle() {
		return scenarioTitle;
	}
	public void setScenarioTitle(String scenarioTitle) {
		this.scenarioTitle = scenarioTitle;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	public Failure getFailure() {
		return failure;
	}
	public void setFailure(Failure failure) {
		this.failure = failure;
	}

	@Override
	public String toString() {
		return "ScenarioResult [scenarioTitle=" + scenarioTitle + ", success="
				+ success + ", failure=" + failure + "]";
	}
	
}
