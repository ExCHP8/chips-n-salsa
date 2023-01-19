/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2023 Vincent A. Cicirello
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

package org.cicirello.search.problems.scheduling;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.permutations.Permutation;
import org.junit.jupiter.api.*;

/** JUnit tests for the WeightedStaticScheduling class. */
public class WeightedStaticTests {

  @Test
  public void testConstructorExceptions() {
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WeightedStaticScheduling(0, 0.5, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WeightedStaticScheduling(1, 0.0, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WeightedStaticScheduling(1, 1.0000001, 0.5));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new WeightedStaticScheduling(1, 0.5, -0.00000001));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new WeightedStaticScheduling(1, 0.5, 1.00000001));
  }

  @Test
  public void testCorrectNumJobs() {
    double[] rdd = {0.25, 0.5, 0.75, 1.0};
    double[] tf = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int n = 1; n < 4; n++) {
      for (int r = 0; r < rdd.length; r++) {
        for (int t = 0; t < tf.length; t++) {
          WeightedStaticScheduling s = new WeightedStaticScheduling(n, rdd[r], tf[t], 42);
          assertEquals(n, s.numberOfJobs());
          assertTrue(s.hasDueDates());
          assertTrue(s.hasWeights());
          assertFalse(s.hasSetupTimes());
          assertFalse(s.hasEarlyWeights());
          assertFalse(s.hasReleaseDates());
          s = new WeightedStaticScheduling(n, rdd[r], tf[t]);
          assertEquals(n, s.numberOfJobs());
          assertTrue(s.hasDueDates());
          assertTrue(s.hasWeights());
          assertFalse(s.hasSetupTimes());
          assertFalse(s.hasEarlyWeights());
          assertFalse(s.hasReleaseDates());
        }
      }
    }
  }

  @Test
  public void testCompletionTimeCalculation() {
    int n = 5;
    double[] rdd = {0.25, 0.5, 0.75, 1.0};
    double[] tf = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int r = 0; r < rdd.length; r++) {
      for (int t = 0; t < tf.length; t++) {
        WeightedStaticScheduling s = new WeightedStaticScheduling(n, rdd[r], tf[t], 42);
        Permutation p1 = new Permutation(new int[] {0, 1, 2, 3, 4});
        int[] c1 = s.getCompletionTimes(p1);
        int expected = 0;
        for (int x = 0; x < n; x++) {
          expected += s.getProcessingTime(x);
          assertEquals(expected, c1[x]);
        }
        Permutation p2 = new Permutation(new int[] {4, 3, 2, 1, 0});
        int[] c2 = s.getCompletionTimes(p2);
        expected = 0;
        for (int x = n - 1; x >= 0; x--) {
          expected += s.getProcessingTime(x);
          assertEquals(expected, c2[x]);
        }
        final int nPlus = n + 1;
        IllegalArgumentException thrown =
            assertThrows(
                IllegalArgumentException.class, () -> s.getCompletionTimes(new Permutation(nPlus)));
      }
    }
  }

  @Test
  public void testConsistencyWithParameters() {
    double[] rdd = {0.25, 0.5, 0.75, 1.0};
    double[] tf = {0.0, 0.25, 0.5, 0.75, 1.0};
    for (int n = 8; n <= 512; n *= 8) {
      for (int r = 0; r < rdd.length; r++) {
        for (int t = 0; t < tf.length; t++) {
          WeightedStaticScheduling s = new WeightedStaticScheduling(n, rdd[r], tf[t], 42);
          int totalP = 0;
          for (int x = 0; x < n; x++) {
            totalP += s.getProcessingTime(x);
          }
          final int MIN_DUEDATE = (int) Math.round(totalP * (1.0 - tf[t] - rdd[r] / 2.0));
          final int MAX_DUEDATE = (int) Math.round(totalP * (1.0 - tf[t] + rdd[r] / 2.0));
          for (int x = 0; x < n; x++) {
            assertTrue(
                s.getProcessingTime(x) >= WeightedStaticScheduling.MIN_PROCESS_TIME
                    && s.getProcessingTime(x) <= WeightedStaticScheduling.MAX_PROCESS_TIME);
            assertTrue(
                s.getWeight(x) >= WeightedStaticScheduling.MIN_WEIGHT
                    && s.getWeight(x) <= WeightedStaticScheduling.MAX_WEIGHT);
            assertTrue(s.getDueDate(x) >= MIN_DUEDATE && s.getDueDate(x) <= MAX_DUEDATE);
          }
          boolean diffP = false;
          for (int x = 1; x < n; x++) {
            if (s.getProcessingTime(x) != s.getProcessingTime(x - 1)) {
              diffP = true;
              break;
            }
          }
          assertTrue(diffP);
          boolean diffW = false;
          for (int x = 1; x < n; x++) {
            if (s.getWeight(x) != s.getWeight(x - 1)) {
              diffW = true;
              break;
            }
          }
          assertTrue(diffW);
          boolean diffD = false;
          for (int x = 1; x < n; x++) {
            if (s.getDueDate(x) != s.getDueDate(x - 1)) {
              diffD = true;
              break;
            }
          }
          assertTrue(diffD);
        }
      }
    }
  }
}
