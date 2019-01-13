package com.main.rules;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.rules.JRip;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/*
Rules using JRip are written in the console
Different rule mining algorithm can be used
*/
public class ClassifyInstance_JRIP {
	private static File fileName;

	public static void main(String args[]) throws Exception {
		createFile("jrip");
		double percentageCorrect = 0;
		String classifierRules = null;

		// file from which rules are generated
		DataSource source = new DataSource("data.arff");

		Instances trainDataset = source.getDataSet();
		// set class index to the last attribute
		trainDataset.setClassIndex(0);
		// get number of classes
		int numClasses = trainDataset.numClasses();
		// print out class values in the training dataset
		for (int i = 0; i < numClasses; i++) {
			// get class string value using the class index
			String classValue = trainDataset.classAttribute().value(i);
			System.out.println("Class Value " + i + " is " + classValue);
		}

		List<String> ignoreTC = new ArrayList<String>();
		for (int i = 0; i < 10000; i++) {
			percentageCorrect = 0;
			Instances trainDataset2 = source.getDataSet();

			trainDataset2.setClassIndex(i);
			System.out.println(trainDataset2.attribute(i).name());
			// System.out.println("Test Case " + (i + 1));
			JRip part = new JRip();

			try {
				part.buildClassifier(trainDataset2);
				Evaluation eval = new Evaluation(trainDataset2);
				eval.crossValidateModel(part, trainDataset2, 5, new Random(1));
				if (eval.pctCorrect() > percentageCorrect) {
					percentageCorrect = eval.pctCorrect();
					classifierRules = part.toString();
				}
				System.out.println(classifierRules + "\n");
			} catch (weka.core.WekaException e) {
				ignoreTC.add(trainDataset2.attribute(i).name());
			}

		}
		// ignored test cases due to lack of sufficient data
		for (String temp : ignoreTC)
			System.out.println(temp);
	}

	public static void createFile(String text) throws Exception {
		fileName = new File("outML" + text + ".txt");

		// if file does not exists, then create it
		if (!fileName.exists()) {
			fileName.createNewFile();
		}
	}

}