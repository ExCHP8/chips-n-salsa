/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2021  Vincent A. Cicirello
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
 
package org.cicirello.search.operators.permutations;

import org.junit.*;
import static org.junit.Assert.*;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.operators.MutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * JUnit 4 test cases for mutation operators on permutations.
 */
public class PermutationMutationTests {
	
	// For tests involving randomness, number of trials to include in test case.
	private static final int NUM_RAND_TESTS = 20;
	
	@Test
	public void testAdjacentSwap() {
		AdjacentSwapMutation m = new AdjacentSwapMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are adjacent swaps
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertEquals("Changed indexes should be off by 1", a, b-1);
				assertEquals("Verify adjacent swap", p.get(a), mutant.get(b));
				assertEquals("Verify adjacent swap", p.get(b), mutant.get(a));
			}
		}
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[] indexes = new boolean[n-1];
			int numSamples = (n-1)*20;
			Permutation p = new Permutation(n);
			for (int i = 0; i < numSamples; i++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				indexes[a] = true;
			}
			for (int i = 0; i < indexes.length; i++) {
				assertTrue("Failed to swap a specific adjacent pair over many trials.", indexes[i]);
			}
		}
	}
	
	@Test
	public void testBlockInterchange() {
		BlockInterchangeMutation m = new BlockInterchangeMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Verify mutations are block interchanges
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b, c, d;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (d = p.length()-1; d >= 0 && p.get(d) == mutant.get(d); d--);
				for (b = a; b < p.length() && p.get(b) != mutant.get(d); b++);
				for (c = d; c > b && p.get(c) != mutant.get(a); c--);
				assertTrue("check for valid block indexes", a <= b && b < c && c <= d);
				int i, j;
				for (i=a, j=c; j <= d; i++, j++) {
					assertEquals("left portion of result", p.get(j), mutant.get(i));
				}
				for (j=b+1; j < c; i++, j++) {
					assertEquals("middle portion of result", p.get(j), mutant.get(i));
				}
				for (j=a; j <= b; j++, i++) {
					assertEquals("right portion of result", p.get(j), mutant.get(i));
				}
			}
		}
		// Check distribution of random indexes
		for (int n = 2; n <= 7; n++) {
			boolean[][] firstPair = new boolean[n-1][n-1];
			boolean[][] secondPair = new boolean[n-1][n-1];
			int numSamples = (n-1)*(n-1)*80;
			int[] indexes = new int[4];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				firstPair[indexes[0]][indexes[1]] = true;
				secondPair[indexes[2]-1][indexes[3]-1] = true;
			}
			checkIndexQuartets(firstPair, secondPair);
		}
	}
	
	@Test
	public void testInsertion() {
		InsertionMutation m = new InsertionMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are insertions
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				if (mutant.get(b) == p.get(a)) {
					for (int i = a; i < b; i++) {
						assertEquals("Verify insertion case: element moved later", p.get(i+1), mutant.get(i));
					}
				} else if (mutant.get(a) == p.get(b)) {
					for (int i = a+1; i <= b; i++) {
						assertEquals("Verify insertion case: element moved earlier", p.get(i-1), mutant.get(i));
					}
				} else {
					fail("Not an insertion.");
				}
			}
		}
	}
	
	@Test
	public void testReversal() {
		ReversalMutation m = new ReversalMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are reversals
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				while (a <= b) {
					assertEquals("Verify mutation is a reversal", p.get(a), mutant.get(b));
					a++;
					b--;
				}
			}
		}
	}
	
	@Test
	public void testSwap() {
		SwapMutation m = new SwapMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are swaps
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				assertEquals("Verify elements swapped", p.get(a), mutant.get(b));
				assertEquals("Verify elements swapped", p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals("Verify interior elements not changed", p.get(i), mutant.get(i));
				}
			}
		}
	}
	
	@Test
	public void testBlockMove() {
		BlockMoveMutation m = new BlockMoveMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][][] indexTriples = new boolean[n][n][n];
			int numSamples = n*(n-1)*(n+1)*40/6;
			int[] indexes = new int[3];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
			}
			checkIndexTriples(indexTriples);
		}
		// Verify mutations are block moves
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				int c;
				for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
				// block of p from index a to index (a+b-c) should be same as mutant index c to index b
				int e = a;
				for (int d = c; d <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
				// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
				for (int d = a; e <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
			}
		}
	}
	
	@Test
	public void testScramble() {
		ScrambleMutation m = new ScrambleMutation();
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testUndoableScramble() {
		UndoableScrambleMutation m = new UndoableScrambleMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testRotation() {
		RotationMutation m = new RotationMutation();
		undoTester(m);
		mutateTester(m);
		splitTester(m);
		Permutation[] testcases = {
			new Permutation(new int[] {0}),
			new Permutation(new int[] {0, 1}), 
			new Permutation(new int[] {0, 2, 1}), 
			new Permutation(new int[] {2, 0, 3, 1})
		};
		Permutation[][] expectedRaw = {
			{},
			{ new Permutation(new int[] {1, 0}) },
			{ new Permutation(new int[] {2, 1, 0}), new Permutation(new int[] {1, 0, 2}) },
			{ new Permutation(new int[] {0, 3, 1, 2}), new Permutation(new int[] {3, 1, 2, 0}), new Permutation(new int[] {1, 2, 0, 3}) }
		};
		ArrayList<HashSet<Permutation>> expected = new ArrayList<HashSet<Permutation>>();
		for (int i = 0; i < expectedRaw.length; i++) {
			HashSet<Permutation> set = new HashSet<Permutation>();
			for (int j = 0; j < expectedRaw[i].length; j++) {
				set.add(expectedRaw[i][j]);
			}
			expected.add(set);
		}
		for (int i = 1; i < testcases.length; i++) {
			for (int j = 0; j < 8; j++) {
				Permutation mutant = new Permutation(testcases[i]);
				m.mutate(mutant);
				assertTrue(expected.get(i).contains(mutant));
			}				
		}
		for (int i = 0; i < testcases.length; i++) {
			Permutation p = new Permutation(testcases[i]);
			final MutationIterator iter = m.iterator(p);
			HashSet<Permutation> observed = new HashSet<Permutation>();
			int count = 0;
			while (iter.hasNext()) {
				iter.nextMutant();
				count++;
				observed.add(p);
			}
			assertEquals(count, observed.size());
			assertEquals(count, expected.get(i).size());
			assertEquals(expected.get(i), observed);
			iter.rollback();
			assertEquals(testcases[i], p);
		}
		for (int i = 1; i < testcases.length; i++) {
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				HashSet<Permutation> observed = new HashSet<Permutation>();
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					observed.add(p);
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				assertEquals(count, observed.size());
				assertEquals(count, expected.get(i).size());
				assertEquals(expected.get(i), observed);
			}
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
						break;
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				IllegalStateException thrown = assertThrows( 
					IllegalStateException.class,
					() -> iter.nextMutant()
				);
			}
			for (int s = 1; s <= expectedRaw[i].length; s++) {
				Permutation p = new Permutation(testcases[i]);
				MutationIterator iter = m.iterator(p);
				int count = 0;
				Permutation pExp = null;
				while (iter.hasNext()) {
					iter.nextMutant();
					count++;
					if (s==count) {
						iter.setSavepoint();
						pExp = new Permutation(p);
					} else if (s==count-1) {
						break;
					}
				}
				iter.rollback();
				assertEquals(pExp, p);
				assertFalse(iter.hasNext());
				iter.rollback();
				assertEquals(pExp, p);
			}
		}
	}
	
	
	@Test
	public void testWindowLimitedInsertion() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are insertions
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
					if (mutant.get(b) == p.get(a)) {
						for (int i = a; i < b; i++) {
							assertEquals("Verify insertion case: element moved later", p.get(i+1), mutant.get(i));
						}
					} else if (mutant.get(a) == p.get(b)) {
						for (int i = a+1; i <= b; i++) {
							assertEquals("Verify insertion case: element moved earlier", p.get(i-1), mutant.get(i));
						}
					} else {
						fail("Not an insertion.");
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedInsertion() {
		WindowLimitedInsertionMutation m = new WindowLimitedInsertionMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are insertions
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				if (mutant.get(b) == p.get(a)) {
					for (int i = a; i < b; i++) {
						assertEquals("Verify insertion case: element moved later", p.get(i+1), mutant.get(i));
					}
				} else if (mutant.get(a) == p.get(b)) {
					for (int i = a+1; i <= b; i++) {
						assertEquals("Verify insertion case: element moved earlier", p.get(i-1), mutant.get(i));
					}
				} else {
					fail("Not an insertion.");
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedReversal() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedReversalMutation m = new WindowLimitedReversalMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are reversals
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
					while (a <= b) {
						assertEquals("Verify mutation is a reversal", p.get(a), mutant.get(b));
						a++;
						b--;
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedReversal() {
		WindowLimitedReversalMutation m = new WindowLimitedReversalMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are reversals
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				while (a <= b) {
					assertEquals("Verify mutation is a reversal", p.get(a), mutant.get(b));
					a++;
					b--;
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedSwap() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedSwapMutation m = new WindowLimitedSwapMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// Verify mutations are swaps
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
					assertEquals("Verify elements swapped", p.get(a), mutant.get(b));
					assertEquals("Verify elements swapped", p.get(b), mutant.get(a));
					for (int i = a+1; i < b; i++) {
						assertEquals("Verify interior elements not changed", p.get(i), mutant.get(i));
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedSwap() {
		WindowLimitedSwapMutation m = new WindowLimitedSwapMutation();
		undoTester(m, 4);
		mutateTester(m, 4);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
		// Verify mutations are swaps
		for (int n = 2; n <= 4; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				assertEquals("Verify elements swapped", p.get(a), mutant.get(b));
				assertEquals("Verify elements swapped", p.get(b), mutant.get(a));
				for (int i = a+1; i < b; i++) {
					assertEquals("Verify interior elements not changed", p.get(i), mutant.get(i));
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedBlockMove() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][][] indexTriples = new boolean[n][n][n];
				int numSamples = n*(n-1)*(n+1)*40/6;
				int[] indexes = new int[3];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
				}
				checkIndexTriples(indexTriples, window);
			}
			// Verify mutations are block moves
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
					int c;
					for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
					// block of p from index a to index (a+b-c) should be same as mutant index c to index b
					int e = a;
					for (int d = c; d <= b; d++, e++) {
						assertEquals(p.get(e), mutant.get(d));
					}
					// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
					for (int d = a; e <= b; d++, e++) {
						assertEquals(p.get(e), mutant.get(d));
					}
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedBlockMove() {
		WindowLimitedBlockMoveMutation m = new WindowLimitedBlockMoveMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 6; n++) {
			boolean[][][] indexTriples = new boolean[n][n][n];
			int numSamples = n*(n-1)*(n+1)*40/6;
			int[] indexes = new int[3];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexTriples[indexes[0]][indexes[1]][indexes[2]] = true;
			}
			checkIndexTriples(indexTriples);
		}
		// Verify mutations are block moves
		for (int n = 2; n <= 6; n++) {
			Permutation p = new Permutation(n);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				int a, b;
				for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
				for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
				assertTrue("verify elements changed", a <= b);
				int c;
				for (c = a+1; c <= b && p.get(a) != mutant.get(c); c++);
				// block of p from index a to index (a+b-c) should be same as mutant index c to index b
				int e = a;
				for (int d = c; d <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
				// block of p from index (a+b-c+1) to index b should be same as mutant index a to index c-1
				for (int d = a; e <= b; d++, e++) {
					assertEquals(p.get(e), mutant.get(d));
				}
			}
		}
	}
	
	@Test
	public void testWindowLimitedScramble() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation(window);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// verify window constraints
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedScramble() {
		WindowLimitedScrambleMutation m = new WindowLimitedScrambleMutation();
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testWindowLimitedUndoableScramble() {
		for (int window = 1; window <= 6; window++) {
			WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation(window);
			undoTester(m);
			mutateTester(m);
			splitTester(m);
			// Check distribution of random indexes
			for (int n = 2; n <= 6; n++) {
				boolean[][] indexPairs = new boolean[n][n];
				int numSamples = n*(n-1)*40;
				int[] indexes = new int[2];
				for (int i = 0; i < numSamples; i++) {
					m.generateIndexes(n, indexes);
					indexPairs[indexes[0]][indexes[1]] = true;
				}
				checkIndexPairs(indexPairs, window);
			}
			// verify window constraints
			for (int n = 2; n <= 6; n++) {
				Permutation p = new Permutation(n);
				for (int t = 0; t < NUM_RAND_TESTS; t++) {
					Permutation mutant = new Permutation(p);
					m.mutate(mutant);
					int a, b;
					for (a = 0; a < p.length() && p.get(a) == mutant.get(a); a++);
					for (b = p.length()-1; b >= 0 && p.get(b) == mutant.get(b); b--);
					assertTrue("verify elements changed", a <= b);
					assertTrue("window violation", b-a <= window);
				}
			}
		}
	}
	
	@Test
	public void testWindowUNlimitedUndoableScramble() {
		WindowLimitedUndoableScrambleMutation m = new WindowLimitedUndoableScrambleMutation();
		undoTester(m, 3);
		mutateTester(m, 3);
		// Check distribution of random indexes
		for (int n = 2; n <= 4; n++) {
			boolean[][] indexPairs = new boolean[n][n];
			int numSamples = n*(n-1)*40;
			int[] indexes = new int[2];
			for (int i = 0; i < numSamples; i++) {
				m.generateIndexes(n, indexes);
				indexPairs[indexes[0]][indexes[1]] = true;
			}
			checkIndexPairs(indexPairs);
		}
	}
	
	@Test
	public void testWindowLimitedMutationConstructorExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedUndoableScrambleMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedScrambleMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedBlockMoveMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedSwapMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedReversalMutation(0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new WindowLimitedInsertionMutation(0)
		);
	}
	
	
	private void undoTester(UndoableMutationOperator<Permutation> m) {
		undoTester(m, 0);
	}
	
	private void undoTester(UndoableMutationOperator<Permutation> m, int minPermLength) {
		// iterate over different length permutations beginning with 0 length
		for (int i = minPermLength; i <= 6; i++) {
			Permutation p = new Permutation(i);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				m.undo(mutant);
				assertEquals("mutate followed by undo should revert to original", p, mutant);
			}
		}
	}
	
	private void mutateTester(MutationOperator<Permutation> m) {
		mutateTester(m, 0);
	}
	
	private void mutateTester(MutationOperator<Permutation> m, int minPermLength) {
		for (int i = minPermLength; i <= 6; i++) {
			Permutation p = new Permutation(i);
			for (int t = 0; t < NUM_RAND_TESTS; t++) {
				Permutation mutant = new Permutation(p);
				m.mutate(mutant);
				// verify mutation produced a valid permutation
				validate(mutant);
				if (i < 2) {
					assertEquals("if length is less than 2, no change should be made", p, mutant);
				} else {
					assertNotEquals("confirm that mutate changed the permutation", p, mutant);
				}
			}
		}
	}
	
	private void validate(Permutation p) {
		boolean[] inP = new boolean[p.length()];
		for (int i = 0; i < inP.length; i++) {
			int j = p.get(i);
			assertTrue("validate permutation element", j >= 0 && j < inP.length);
			inP[j] = true;
		}
		for (int i = 0; i < inP.length; i++) {
			assertTrue("confirm no elements are missing", inP[i]);
		}
	}
	
	private void splitTester(MutationOperator<Permutation> m) {
		MutationOperator<Permutation> s = m.split();
		assertEquals("verify that runtime class of split is the same", m.getClass(), s.getClass());
		if (m instanceof UndoableMutationOperator) {
			for (int i = 0; i < 10; i++) {
				Permutation p1 = new Permutation(10);
				Permutation p2 = p1.copy();
				Permutation p3 = p1.copy();
				m.mutate(p2);
				assertNotEquals(p1, p2);
				UndoableMutationOperator<Permutation> m2 = (UndoableMutationOperator<Permutation>)m.split();
				m2.mutate(p3);
				assertNotEquals(p1, p3);
				((UndoableMutationOperator<Permutation>)m).undo(p2);
				assertEquals(p1, p2);
				m2.undo(p3);
				assertEquals(p1, p3);
			}
		}
	}
	
	private void checkIndexPairs(boolean[][] pairs) {
		// verify that each pair was generated over many trials
		for (int i = 0; i < pairs.length; i++) {
			for (int j = 0; j < pairs.length; j++) {
				if (i!=j) {
					assertTrue("failed to generate: (" + i + ", " + j + ")", pairs[i][j]);
				}
			}
		}
	}
	
	private void checkIndexPairs(boolean[][] pairs, int window) {
		// verify that each pair within window was generated over many trials
		// and verify no window violations
		for (int i = 0; i < pairs.length; i++) {
			for (int j = 0; j < pairs.length; j++) {
				if (i!=j) {
					if (Math.abs(i-j) <= window) {
						assertTrue("failed to generate: w=" + window + " pair=(" + i + ", " + j + ")", pairs[i][j]);
					} else {
						assertFalse("window violation: w=" + window + " pair=(" + i + ", " + j + ")", pairs[i][j]);
					}
				}
			}
		}
	}
	
	private void checkIndexTriples(boolean[][][] triples) {
		// verify that each triple was generated over many trials
		for (int i = 0; i < triples.length; i++) {
			for (int j = i+1; j < triples.length; j++) {
				for (int k = j; k < triples.length; k++) {
					assertTrue("failed to generate: (" + i + ", " + j + ", " + k + ")", triples[i][j][k]);
				}
			}
		}
	}
	
	private void checkIndexTriples(boolean[][][] triples, int window) {
		// verify that each triple was generated over many trials
		// and verify no window violations
		for (int i = 0; i < triples.length; i++) {
			for (int j = i+1; j < triples.length; j++) {
				for (int k = j; k < triples.length; k++) {
					if (k-i <= window) {
						assertTrue("failed to generate: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")", triples[i][j][k]);
					} else {
						assertFalse("window violation: w=" + window + " triple=(" + i + ", " + j + ", " + k + ")", triples[i][j][k]);
					}
				}
			}
		}
	}
	
	private void checkIndexQuartets(boolean[][] firstPair, boolean[][] secondPair) {
		// verify that each of the possible left blocks was generated at least once
		// and each of the possible right blocks was generated at least once over
		// many trials.
		for (int i = 0; i < firstPair.length; i++) {
			for (int j = i; j < firstPair[i].length; j++) {
				assertTrue("failed to generate left pair: (" + i + ", " + j + ")", firstPair[i][j]);
				assertTrue("failed to generate right pair: (" + (i+1) + ", " + (j+1) + ")", secondPair[i][j]);
			}
		}
	}
}