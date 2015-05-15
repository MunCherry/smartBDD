package cs.vt.edu.bdd.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

public class EChunker {
	
	/*
	 * @param: text
	 * chunking an input text
	 */
	public void chunk(String text) {
		
		try {
			POSModel model = new POSModelLoader().load(new File("Models/en-pos-maxent.bin"));
			PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
			POSTaggerME tagger = new POSTaggerME(model);

			//String input = "Hi. How are you? This is Mike.";
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new StringReader(text));

			perfMon.start();
			String line;
			String whitespaceTokenizerLine[] = null;

			String[] tags = null;
			while ((line = lineStream.read()) != null) {

				whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
						.tokenize(line);
				tags = tagger.tag(whitespaceTokenizerLine);

				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
				System.out.println(sample.toString());

				perfMon.incrementCounter();
			}
			perfMon.stopAndPrintFinalResult();

			// chunker
			InputStream is = new FileInputStream("Models/en-chunker.bin");
			ChunkerModel cModel = new ChunkerModel(is);

			ChunkerME chunkerME = new ChunkerME(cModel);
			String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);

			for (String s : result)
				System.out.println(s);

			Span[] span = chunkerME.chunkAsSpans(whitespaceTokenizerLine, tags);
			for (Span s : span)
				System.out.println(s.toString());


		} catch (IOException e){
			e.printStackTrace();
		}		
	}
}
