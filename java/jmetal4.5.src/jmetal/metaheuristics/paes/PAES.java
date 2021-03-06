//  PAES.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.paes;

import java.io.File;
import java.util.Comparator;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.archive.AdaptiveGridArchive;
import jmetal.util.comparators.DominanceComparator;

/**
 * This class implements the PAES algorithm.
 */
public class PAES extends Algorithm {

	File fileName;

	/**
	 * Create a new PAES instance for resolve a problem
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public PAES(Problem problem) {
		super(problem);
	} // Paes

	/**
	 * Tests two solutions to determine which one becomes be the guide of PAES
	 * algorithm
	 * 
	 * @param solution
	 *            The actual guide of PAES
	 * @param mutatedSolution
	 *            A candidate guide
	 */
	public Solution test(Solution solution, Solution mutatedSolution, AdaptiveGridArchive archive) {

		int originalLocation = archive.getGrid().location(solution);
		int mutatedLocation = archive.getGrid().location(mutatedSolution);

		if (originalLocation == -1) {
			return new Solution(mutatedSolution);
		}

		if (mutatedLocation == -1) {
			return new Solution(solution);
		}

		if (archive.getGrid().getLocationDensity(mutatedLocation) < archive.getGrid()
				.getLocationDensity(originalLocation)) {
			return new Solution(mutatedSolution);
		}

		return new Solution(solution);
	} // test

	/**
	 * Runs of the Paes algorithm.
	 * 
	 * @return a <code>SolutionSet</code> that is a set of non dominated solutions
	 *         as a result of the algorithm execution
	 * @throws JMException
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		int bisections, archiveSize, maxEvaluations, evaluations;
		AdaptiveGridArchive archive;
		Operator mutationOperator;
		Comparator dominance;

		// Read the params
		bisections = ((Integer) this.getInputParameter("biSections")).intValue();
		archiveSize = ((Integer) this.getInputParameter("archiveSize")).intValue();
		maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();

		// Read the operators
		mutationOperator = this.operators_.get("mutation");

		// Initialize the variables
		evaluations = 0;
		archive = new AdaptiveGridArchive(archiveSize, bisections, problem_.getNumberOfObjectives());
		dominance = new DominanceComparator();

		// -> Create the initial solution and evaluate it and his constraints
		Solution solution = new Solution(problem_);
		problem_.evaluate(solution);
		problem_.evaluateConstraints(solution);
		evaluations++;

		// Add it to the archive
		archive.add(new Solution(solution));
		// from here to HV one run
		// int fileNum = 0, num = 0;
		// try {
		// createFile(fileNum);
		// FileWriter fwTime = new FileWriter(fileName.getAbsoluteFile(), true);
		// BufferedWriter fileTime = new BufferedWriter(fwTime);
		// for (int i = 0; i < archive.size(); i++) {
		//
		// fileTime.write(archive.get(i).getObjective(0) + " "
		// + archive.get(i).getObjective(1) + " "
		// + archive.get(i).getObjective(2));
		// fileTime.write("\n");
		// }
		// fileTime.close();
		// fileNum++;
		// num += 1000;
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Iterations....
		int count = 0;
		do {
			// Create the mutate one
			Solution mutatedIndividual = new Solution(solution);
			mutationOperator.execute(mutatedIndividual);

			problem_.evaluate(mutatedIndividual);
			problem_.evaluateConstraints(mutatedIndividual);
			evaluations++;
			// <-

			// Check dominance
			int flag = dominance.compare(solution, mutatedIndividual);

			if (flag == 1) { // If mutate solution dominate
				solution = new Solution(mutatedIndividual);
				archive.add(mutatedIndividual);
				count++;
			} else if (flag == 0) { // If none dominate the other
				if (archive.add(mutatedIndividual)) {
					solution = test(solution, mutatedIndividual, archive);
				}
			}
			/*
			 * if ((evaluations % 100) == 0) {
			 * archive.printObjectivesToFile("FUN"+evaluations) ;
			 * archive.printVariablesToFile("VAR"+evaluations) ;
			 * archive.printObjectivesOfValidSolutionsToFile("FUNV"+evaluations) ; }
			 */
			// if (evaluations >= num) {
			// try {
			// createFile(fileNum);
			// FileWriter fwTime = new FileWriter(fileName.getAbsoluteFile(), true);
			// BufferedWriter fileTime = new BufferedWriter(fwTime);
			// for (int i = 0; i < archive.size(); i++) {
			//
			// fileTime.write(archive.get(i).getObjective(0) + " " +
			// archive.get(i).getObjective(1) + " "
			// + archive.get(i).getObjective(2));
			// fileTime.write("\n");
			// }
			// fileTime.close();
			// fileNum++;
			// num += 1000;
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }

		} while (evaluations < maxEvaluations);
		// System.out.println(count);
		// do {
		// // Create the mutate one
		// Solution mutatedIndividual = new Solution(solution);
		// mutationOperator.execute(mutatedIndividual);
		//
		// problem_.evaluate(mutatedIndividual);
		// problem_.evaluateConstraints(mutatedIndividual);
		// evaluations++;
		// // <-
		//
		// // Check dominance
		// int flag = dominance.compare(solution, mutatedIndividual);
		//
		// if (flag == 1) { // If mutate solution dominate
		// solution = new Solution(mutatedIndividual);
		// archive.add(mutatedIndividual);
		// } else if (flag == 0) { // If none dominate the other
		// if (archive.add(mutatedIndividual)) {
		// solution = test(solution, mutatedIndividual, archive);
		// }
		// }
		// System.out.println(evaluations);
		// } while (archive.size() < 100);
		// Return the population of non-dominated solution
		archive.printFeasibleFUN("FUN_PAES");
		// System.out.println(archive.size());
		return archive;
	} // execute

	// Dipesh
	public void printObj(SolutionSet population) {
		double obj1 = 0, obj2 = 0, obj3 = 0, obj4 = 0;

		for (int i = 0; i < population.size(); i++) {
			obj1 += population.get(i).getObjective(0);
			obj2 += population.get(i).getObjective(1);
			obj3 += population.get(i).getObjective(2);
			obj4 += population.get(i).getObjective(3);
		}
		System.out.println(obj1 / population.size() + "\t" + obj2 / population.size() + "\t" + obj3 / population.size()
				+ "\t" + obj4 / population.size());
	}

	public void createFile(int count) throws Exception {
		// fileName = new File("output/ciscoMinim/OneRun/paes/paes" + count);
		// fileName = new File("output/ciscoPrior/OneRun/paes/paes" + count);
		// fileName = new File("output/kongsbergSelect/OneRun/paes/paes" +
		// count);
		// fileName = new File(
		// "output/defects4j/minimization/chart/OneRun/paes/paes" + count);
		// fileName = new File(
		// "output/defects4j/prioritization/time/OneRun/paes/paes" + count);
		// fileName = new
		// File("output/defects4j/selection/math/OneRun/paes/paes"
		// + count);
		fileName = new File("output/testingResourceAllocation/OneRun/paes/paes" + count);
		// fileName = new File(
		// "output/IntegrationTestOrderProblem/OneRun/paes/paes" + count);
		// if file does not exists, then create it
		if (!fileName.exists()) {
			fileName.createNewFile();
		}
	}
} // PAES
