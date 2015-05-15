package cs.vt.edu.bdd.editors;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;

public class ScenarioScanner extends RuleBasedScanner {

	public ScenarioScanner(ColorManager manager) {
		IToken sceTitle =
			new Token(
				new TextAttribute(
					manager.getColor(IScenarioColorConstants.Sce_TITLE), null, SWT.BORDER_SOLID));
		
		IToken given =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.GIVEN), null, SWT.BORDER_SOLID));
		
		
		IToken andOR =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.AND_OR), null, SWT.BORDER_SOLID));
		
		IToken when =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.WHEN), null, SWT.BORDER_SOLID));
		
		IToken then =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.THEN),null, SWT.BORDER_SOLID));
		
		IToken number =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.NUMBER),null, SWT.BORDER_SOLID));
		
		IToken quote =
				new Token(
					new TextAttribute(
						manager.getColor(IScenarioColorConstants.NUMBER),null, SWT.BORDER_SOLID));
		

		IRule[] rules = new IRule[9];
		//Add rule for scenario title
		rules[0] = new SingleLineRule("Scenario", ":", sceTitle);

		rules[1] = new SingleLineRule("Given", " ", given);
		
		rules[2] = new SingleLineRule("And", " ", andOR);
		
		rules[3] = new SingleLineRule("Or", "", andOR);
		
		rules[4] = new SingleLineRule("When", " ", when);
		
		rules[5] = new SingleLineRule("Then", " ", then);
		
		rules[6] = new SingleLineRule("$", "$", number);
		
		rules[7] = new SingleLineRule("\"", "\"", quote);
		
		
		// Add generic whitespace rule.
		rules[8] = new WhitespaceRule(new ScenarioWhitespaceDetector());

		setRules(rules);
	}
}
