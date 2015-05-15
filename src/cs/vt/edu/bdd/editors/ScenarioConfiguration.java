package cs.vt.edu.bdd.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class ScenarioConfiguration extends SourceViewerConfiguration {
	private ScenarioDoubleClickStrategy doubleClickStrategy;
	private ScenarioTagScanner tagScanner;
	private ScenarioScanner scanner;
	private ColorManager colorManager;

	public ScenarioConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			ScenarioPartitionScanner.XML_COMMENT,
			ScenarioPartitionScanner.XML_TAG };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new ScenarioDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected ScenarioScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new ScenarioScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IScenarioColorConstants.DEFAULT))));
		}
		return scanner;
	}
	protected ScenarioTagScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new ScenarioTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IScenarioColorConstants.TAG))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getXMLTagScanner());
		reconciler.setDamager(dr, ScenarioPartitionScanner.XML_TAG);
		reconciler.setRepairer(dr, ScenarioPartitionScanner.XML_TAG);

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IScenarioColorConstants.XML_COMMENT)));
		reconciler.setDamager(ndr, ScenarioPartitionScanner.XML_COMMENT);
		reconciler.setRepairer(ndr, ScenarioPartitionScanner.XML_COMMENT);

		return reconciler;
	}

}