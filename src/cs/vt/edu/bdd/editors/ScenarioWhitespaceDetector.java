package cs.vt.edu.bdd.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class ScenarioWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
