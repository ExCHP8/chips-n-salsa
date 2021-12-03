/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021 Vincent A. Cicirello
 *
 * This file is part of Chips-n-Salsa (https://chips-n-salsa.cicirello.org/).
 * 
 * Chips-n-Salsa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Chips-n-Salsa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.cicirello.search.evo;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * JUnit 4 test cases for selection operators.
 */
public class SelectionOperatorTests {
	
	@Test
	public void testRandomSelection() {
		RandomSelection selection = new RandomSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		RandomSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
	}
	
	@Test
	public void testTournamentSelection() {
		for (int k = 2; k <= 4; k++) {
			TournamentSelection selection = new TournamentSelection(k);
			validateIndexes_Double(selection);
			validateIndexes_Integer(selection);
			TournamentSelection selection2 = selection.split();
			validateIndexes_Double(selection2);
			validateIndexes_Integer(selection2);
		}
		
		TournamentSelection selection = new TournamentSelection(4);
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new TournamentSelection(1)
		);
	}
	
	@Test
	public void testBinaryTournamentSelection() {
		TournamentSelection selection = new TournamentSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		TournamentSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
	}
	
	@Test
	public void testFitnessProportionateSelection() {
		FitnessProportionateSelection selection = new FitnessProportionateSelection();
		validateIndexes_Double(selection);
		validateIndexes_Integer(selection);
		FitnessProportionateSelection selection2 = selection.split();
		validateIndexes_Double(selection2);
		validateIndexes_Integer(selection2);
		
		validateHigherFitnessSelectedMoreOften_Double(selection);
		validateHigherFitnessSelectedMoreOften_Integer(selection);
		
		double[] weights = selection.computeWeightRunningSum(new PopFitVectorDoubleSimple(5));
		double[] expected = {1, 3, 6, 10, 15};
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
		
		weights = selection.computeWeightRunningSum(new PopFitVectorIntegerSimple(5));
		assertEquals(5, weights.length);
		for (int i = 0; i < weights.length; i++) {
			assertEquals(expected[i], weights[i], 1E-10);
		}
	}
	
	private void validateHigherFitnessSelectedMoreOften_Double(SelectionOperator selection) {
		// This part of the test attempts to confirm that greater weight is placed on
		// higher fitness population members. A sporadic failure is not necessarily a 
		// real failure, but it should fail with low probability.
		PopFitVectorDouble pf_d = new PopFitVectorDouble(10);
		int[] selected = new int[20];
		selection.select(pf_d, selected);
		int countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] >= pf_d.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
	}
	
	private void validateHigherFitnessSelectedMoreOften_Integer(SelectionOperator selection) {
		// This part of the test attempts to confirm that greater weight is placed on
		// higher fitness population members. A sporadic failure is not necessarily a 
		// real failure, but it should fail with low probability.
		PopFitVectorInteger pf_int = new PopFitVectorInteger(10);
		int[] selected = new int[20];
		selection.select(pf_int, selected);
		int countLarger = 0;
		for (int i = 0; i < selected.length; i++) {
			if (selected[i] >= pf_int.size() / 2) countLarger++;
		}
		assertTrue(countLarger > selected.length/2);
	}
	
	private void validateIndexes_Double(SelectionOperator selection) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorDouble pf = new PopFitVectorDouble(s);
			int[] count = new int[s];
			for (int i = Math.max(0, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (s >= 4) {
				boolean different = false;
				for (int i = 1; i < s && !different; i++) {
					if (count[i] != count[i-1]) {
						different = true;
					}
				}
				assertTrue(different);
			}
		}
	}
	
	private void validateIndexes_Integer(SelectionOperator selection) {
		selection.init(17);
		for (int s = 1; s <= 8; s *= 2) {
			PopFitVectorInteger pf = new PopFitVectorInteger(s);
			int[] count = new int[s];
			for (int i = Math.max(0, s-2); i <= s+2; i++) {
				int[] selected = new int[i];
				selection.select(pf, selected);
				for (int j = 0; j < selected.length; j++) {
					assertTrue(selected[j] >= 0);
					assertTrue(selected[j] < s);
					count[selected[j]]++;
				}
			}
			if (s >= 4) {
				boolean different = false;
				for (int i = 1; i < s && !different; i++) {
					if (count[i] != count[i-1]) {
						different = true;
					}
				}
				assertTrue(different);
			}
		}
	}
	
	private static class PopFitVectorDouble implements PopulationFitnessVector.Double {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorDouble(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 << i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public double getFitness(int i) {
			return fitnesses[i];
		}
	}
	
	private static class PopFitVectorInteger implements PopulationFitnessVector.Integer {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorInteger(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 << i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public int getFitness(int i) {
			return fitnesses[i];
		}
	}
	
	private static class PopFitVectorDoubleSimple implements PopulationFitnessVector.Double {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorDoubleSimple(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 + i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public double getFitness(int i) {
			return fitnesses[i];
		}
	}
	
	private static class PopFitVectorIntegerSimple implements PopulationFitnessVector.Integer {
		
		private int s;
		private int[] fitnesses;
		
		public PopFitVectorIntegerSimple(int size) {
			s = size;
			fitnesses = new int[s];
			for (int i = 0; i < s; i++) {
				fitnesses[i] = 1 + i;
			}
		}
		
		@Override
		public int size() { return s; }
		
		@Override
		public int getFitness(int i) {
			return fitnesses[i];
		}
	}
}
