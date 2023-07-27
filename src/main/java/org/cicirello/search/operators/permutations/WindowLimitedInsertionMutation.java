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

package org.cicirello.search.operators.permutations;

import org.cicirello.math.rand.EnhancedSplittableGenerator;
import org.cicirello.permutations.Permutation;
import org.cicirello.search.internal.RandomnessFactory;
import org.cicirello.search.operators.IterableMutationOperator;
import org.cicirello.search.operators.MutationIterator;
import org.cicirello.search.operators.UndoableMutationOperator;

/**
 * This class implements a window-limited version of the {@link InsertionMutation} mutation operator
 * on permutations. A window-limited mutation operator on permutations is a mutation operator such
 * that there is a window constraint, w, on the random indexes used by the mutation. For a mutation
 * operator that uses a pair of random indexes, (i, j), these indexes are constrained such that
 * |i-j| &le; w. All index pairs that satisfy the window constraint are equally likely.
 *
 * <p>The runtime (worst case and average case) of both the {@link #mutate(Permutation) mutate} and
 * {@link #undo(Permutation) undo} methods is O(min(n,w)), where n is the length of the permutation.
 * Since chosen indexes can be no more than w positions apart, at most (w+1) elements are moved.
 *
 * <p>For further discussion of window limited permutation mutation operators see:<br>
 * V. A. Cicirello, <a href="https://www.cicirello.org/publications/cicirello2014bict.html"
 * target=_top>"On the Effects of Window-Limits on the Distance Profiles of Permutation Neighborhood
 * Operators,"</a> in Proceedings of the 8th International Conference on Bioinspired Information and
 * Communications Technologies, pages 28-35, December 2014. <a
 * href="https://www.cicirello.org/publications/cicirello-bict-2014.pdf">[PDF]</a> <a
 * href="https://www.cicirello.org/publications/cicirello2014bict.bib">[BIB]</a> <a
 * href="http://dl.acm.org/citation.cfm?id=2744531">[From the ACM Digital Library]</a>
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class WindowLimitedInsertionMutation
    implements UndoableMutationOperator<Permutation>, IterableMutationOperator<Permutation> {

  private final int limit;
  private final EnhancedSplittableGenerator generator;

  // needed to implement undo
  private final int[] indexes;

  /**
   * Constructs a WindowLimitedInsertionMutation mutation operator with a default window limit of
   * Integer.MAX_VALUE.
   */
  public WindowLimitedInsertionMutation() {
    this(Integer.MAX_VALUE);
  }

  /**
   * Constructs a WindowLimitedInsertionMutation mutation operator.
   *
   * @param windowLimit The window limit, which must be positive.
   * @throws IllegalArgumentException if windowLimit &le; 0
   */
  public WindowLimitedInsertionMutation(int windowLimit) {
    if (windowLimit <= 0) throw new IllegalArgumentException("window limit must be positive");
    limit = windowLimit;
    generator = RandomnessFactory.createEnhancedSplittableGenerator();
    indexes = new int[2];
  }

  private WindowLimitedInsertionMutation(WindowLimitedInsertionMutation other) {
    limit = other.limit;
    generator = other.generator.split();
    indexes = new int[2];
  }

  @Override
  public void mutate(Permutation c) {
    if (c.length() >= 2) {
      generateIndexes(c.length(), indexes);
      c.removeAndInsert(indexes[0], indexes[1]);
    }
  }

  @Override
  public void undo(Permutation c) {
    if (c.length() >= 2) {
      c.removeAndInsert(indexes[1], indexes[0]);
    }
  }

  @Override
  public WindowLimitedInsertionMutation split() {
    return new WindowLimitedInsertionMutation(this);
  }

  /**
   * Creates and returns a {@link MutationIterator} that can be used to systematically iterate over
   * all of the direct neighbors (i.e., a single mutation step away) of a candidate solution, as one
   * might do in a hill climber.
   *
   * <p>The worst case runtime of the {@link MutationIterator#hasNext} and the {@link
   * MutationIterator#setSavepoint} methods of the {@link MutationIterator} created by this method
   * is O(1). The amortized runtime of the {@link MutationIterator#nextMutant} method is O(1). And
   * the worst case runtime of the {@link MutationIterator#rollback} method is O(min(n,w)), where n
   * is the length of the Permutation, and w is the window limit.
   *
   * @param p The candidate solution subject to the mutation. Calling methods of the {@link
   *     MutationIterator} that is returned changes the state of p. See the documentation of those
   *     methods for details of how such changes may occur.
   * @return A MutationIterator for iterating over the direct neighbors of p.
   */
  @Override
  public MutationIterator iterator(Permutation p) {
    return new WindowLimitedInsertionIterator(p, limit);
  }

  /*
   * package access to support unit testing
   */
  void generateIndexes(int n, int[] indexes) {
    if (limit >= n) {
      generator.nextIntPair(n, indexes);
    } else {
      generator.nextWindowedIntPair(n, limit, indexes);
    }
  }
}
