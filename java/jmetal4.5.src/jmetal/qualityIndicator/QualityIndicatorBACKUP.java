package jmetal.qualityIndicator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.NonDominatedSolutionList;

public class QualityIndicatorBACKUP {

	int problemcount;
	int startindex;
	String[] indicatorList_;
	int independentRuns_;
	// private static String inFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/defects4j/prioritization/";
	// private static String outFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/defects4j/prioritization/referenceFront/";

	// private static String inFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/defects4j/selection/";
	// private static String outFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/defects4j/selection/referenceFront/";

	// private static String inFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/defects4j/prioritization/";
	// private static String outFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/defects4j/prioritization/referenceFront/";

	// private static String inFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/kongsbergSelect/";
	// private static String outFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/kongsbergSelect/referenceFront/";

	// private static String inFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/ciscoPrior/";
	// private static String outFolderNSGAII =
	// "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/ciscoPrior/referenceFront/";

	private static String inFolderNSGAII = "/Users/Dipesh/OneDrive/Papers/Journal ICST/Result/IntegrationTestOrderProblem/";
	private static String outFolderNSGAII = "/Users/Dipesh/OneDrive/Papers/Journal ICST/Analysis/IntegrationTestOrderProblem/referenceFront/";

	static String[] algorithmNameList_ = new String[] { "cbea_consistent",
			"cbea", "mocell", "nsgaii", "paes", "spea2" };

	public QualityIndicatorBACKUP() {
		// TODO Auto-generated constructor stub
		// problemcount = 1456;
		independentRuns_ = 50;
		// algorithmNameList_ = new String[] { "ea"};
		indicatorList_ = new String[] { "HV", "SPREAD", "EPSILON" };
	}

	public void generateReferenceFronts() {
		generateReferenceFront();
	}

	private void generateReferenceFront() {
		String frontPath_ = outFolderNSGAII + "nondominated";

		MetricsUtil metricsUtils = new MetricsUtil();
		NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
		for (int algorithmIndex = 0; algorithmIndex < algorithmNameList_.length; algorithmIndex++) {

			for (int numRun = 1; numRun <= independentRuns_; numRun++) {
				String outputParetoFrontFilePath;
				outputParetoFrontFilePath = inFolderNSGAII
						+ algorithmNameList_[algorithmIndex] + "/"
						+ algorithmNameList_[algorithmIndex] + numRun;
				String solutionFrontFile = outputParetoFrontFilePath;

				metricsUtils.readNonDominatedSolutionSet(solutionFrontFile,
						solutionSet);
			} // for
		} // for
		solutionSet.printObjectivesToFile(frontPath_);
	}

	public void generateQualityIndicators() throws IOException {
		String path = "C:\\nonconformity\\";
		if (indicatorList_.length > 0) {

			for (int algorithmIndex = 0; algorithmIndex < algorithmNameList_.length; algorithmIndex++) {

				String algorithmDirectory;
				algorithmDirectory = path + algorithmNameList_[algorithmIndex];

				for (int indicatorIndex = 0; indicatorIndex < indicatorList_.length; indicatorIndex++) {
					String qualityIndicatorFile = path + "QualityIndicator\\"
							+ algorithmNameList_[algorithmIndex] + "_"
							+ indicatorList_[indicatorIndex] + ".txt";
					FileWriter os = new FileWriter(
							qualityIndicatorFile.toString());
					for (int problemIndex = 1; problemIndex <= problemcount; problemIndex++) {

						String paretoFrontPath = path + "referenceFronts\\"
								+ problemIndex + ".rf";

						// for (int indicatorIndex = 0; indicatorIndex <
						// indicatorList_.length; indicatorIndex++) {
						System.out.println("Experiment - Quality indicator: "
								+ indicatorList_[indicatorIndex]);

						// resetFile(problemDirectory + "\\" +
						// indicatorList_[indicatorIndex]);

						for (int numRun = 1; numRun <= independentRuns_; numRun++) {
							String problemDirectory = algorithmDirectory
									+ numRun;
							String outputParetoFrontFilePath;
							outputParetoFrontFilePath = problemDirectory
									+ "\\FUN_" + problemIndex + "_0.txt";
							String solutionFrontFile = outputParetoFrontFilePath;

							double value = 0;

							if (indicatorList_[indicatorIndex].equals("HV")) {

								Hypervolume indicators = new Hypervolume();
								double[][] solutionFront = indicators.utils_
										.readFront(solutionFrontFile);
								double[][] trueFront = indicators.utils_
										.readFront(paretoFrontPath);
								value = indicators.hypervolume(solutionFront,
										trueFront, trueFront[0].length);

								qualityIndicatorFile = qualityIndicatorFile
										+ "\\HV";

							}
							if (indicatorList_[indicatorIndex].equals("SPREAD")) {
								Spread indicators = new Spread();
								double[][] solutionFront = indicators.utils_
										.readFront(solutionFrontFile);
								double[][] trueFront = indicators.utils_
										.readFront(paretoFrontPath);
								value = indicators.spread(solutionFront,
										trueFront, trueFront[0].length);

								qualityIndicatorFile = qualityIndicatorFile
										+ "\\SPREAD";
							}
							if (indicatorList_[indicatorIndex].equals("IGD")) {
								InvertedGenerationalDistance indicators = new InvertedGenerationalDistance();
								double[][] solutionFront = indicators.utils_
										.readFront(solutionFrontFile);
								double[][] trueFront = indicators.utils_
										.readFront(paretoFrontPath);
								value = indicators
										.invertedGenerationalDistance(
												solutionFront, trueFront,
												trueFront[0].length);

								qualityIndicatorFile = qualityIndicatorFile
										+ "\\IGD";
							}
							if (indicatorList_[indicatorIndex]
									.equals("EPSILON")) {
								Epsilon indicators = new Epsilon();
								double[][] solutionFront = indicators.utils_
										.readFront(solutionFrontFile);
								double[][] trueFront = indicators.utils_
										.readFront(paretoFrontPath);
								value = indicators.epsilon(solutionFront,
										trueFront, trueFront[0].length);

								qualityIndicatorFile = qualityIndicatorFile
										+ "\\EPSILON";
							}

							if (!qualityIndicatorFile.equals(problemDirectory)) {
								try {
									os.write(value + "\t");
								} catch (IOException ex) {
									Logger.getLogger(Experiment.class.getName())
											.log(Level.SEVERE, null, ex);
								}
							} // if
						} // for
						os.write("\r\n");
					} // for
					os.close();
					System.out.println("Finish a file");
				} // for

			} // for
		} // if
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		QualityIndicatorBACKUP qi = new QualityIndicatorBACKUP();
		qi.generateReferenceFronts();
		// qi.generateQualityIndicators();
	}

}
