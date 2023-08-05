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

package org.cicirello.search.operators.integers;

import static org.junit.jupiter.api.Assertions.*;

import org.cicirello.search.representations.IntegerVector;
import org.cicirello.search.representations.SingleInteger;
import org.junit.jupiter.api.*;

/** JUnit test cases for the classes that implement Initializer for the IntegerValued classes. */
public class IntegerValuedInitializerTests {

  // For tests involving randomness, number of test samples.
  private final int NUM_SAMPLES = 100;

  @Test
  public void testIntegerVectorInitializerEquals() {
    IntegerVectorInitializer f = new IntegerVectorInitializer(2, 2, 4, 0, 8);
    IntegerVectorInitializer g = new IntegerVectorInitializer(2, 2, 4, 0, 8);
    IntegerVectorInitializer f1 = new IntegerVectorInitializer(2, 1, 4, 0, 8);
    IntegerVectorInitializer f2 = new IntegerVectorInitializer(2, 2, 5, 0, 8);
    IntegerVectorInitializer f3 = new IntegerVectorInitializer(2, 2, 4, 1, 8);
    IntegerVectorInitializer f4 = new IntegerVectorInitializer(2, 2, 4, 0, 9);
    assertEquals(f, g);
    assertEquals(f.hashCode(), g.hashCode());
    assertNotEquals(f, f1);
    assertNotEquals(f, f2);
    assertNotEquals(f, f3);
    assertNotEquals(f, f4);
    assertNotEquals(f, null);
    assertNotEquals(f, "hello");
    f = new IntegerVectorInitializer(2, 2, 4);
    g = new IntegerVectorInitializer(2, 2, 4);
    f1 = new IntegerVectorInitializer(2, 1, 4);
    f2 = new IntegerVectorInitializer(2, 2, 5);
    f3 = new IntegerVectorInitializer(2, 2, 4, 0, 8);
    assertEquals(f, g);
    assertEquals(f.hashCode(), g.hashCode());
    assertNotEquals(f, f1);
    assertNotEquals(f, f2);
    assertNotEquals(f, f3);
  }

  @Test
  public void testBoundedIntegerEquals() {
    IntegerValueInitializer f = new IntegerValueInitializer(2, 3, 2, 2);
    SingleInteger g1 = f.createCandidateSolution();
    SingleInteger g2 = f.createCandidateSolution();
    SingleInteger h = new SingleInteger(2);
    assertTrue(g1.equals(h));
    assertEquals(g1, g2);
    assertEquals(g1.hashCode(), g2.hashCode());
    IntegerValueInitializer f2 = new IntegerValueInitializer(2, 3, 2, 2);
    SingleInteger g3 = f2.createCandidateSolution();
    assertEquals(g1, g3);
    assertEquals(g1.hashCode(), g3.hashCode());
    f2 = new IntegerValueInitializer(2, 3, 2, 3);
    assertEquals(g1, f2.createCandidateSolution());
    f2 = new IntegerValueInitializer(2, 3, 0, 2);
    assertEquals(g1, f2.createCandidateSolution());
    assertFalse(g1.equals(null));
    f2 = new IntegerValueInitializer(4, 5, 2, 4);
    assertFalse(g1.equals(f2.createCandidateSolution()));
  }

  @Test
  public void testBoundedIntegerVectorEquals() {
    IntegerVectorInitializer f =
        new IntegerVectorInitializer(
            new int[] {2, 2}, new int[] {3, 3}, new int[] {2, 2}, new int[] {2, 2});
    IntegerVector g1 = f.createCandidateSolution();
    IntegerVector g2 = f.createCandidateSolution();
    IntegerVector h = new IntegerVector(new int[] {2});
    assertNotEquals(g1, h);
    assertEquals(g1, g2);
    assertEquals(g1.hashCode(), g2.hashCode());
    IntegerVectorInitializer f2 =
        new IntegerVectorInitializer(
            new int[] {2, 2}, new int[] {3, 3}, new int[] {2, 2}, new int[] {2, 2});
    IntegerVector g3 = f2.createCandidateSolution();
    assertEquals(g1, g3);
    assertEquals(g1.hashCode(), g3.hashCode());
    f2 =
        new IntegerVectorInitializer(
            new int[] {2, 2}, new int[] {3, 3}, new int[] {2, 2}, new int[] {3, 3});
    assertNotEquals(g1, f2.createCandidateSolution());
    f2 =
        new IntegerVectorInitializer(
            new int[] {2, 2}, new int[] {3, 3}, new int[] {0, 0}, new int[] {2, 2});
    assertNotEquals(g1, f2.createCandidateSolution());
    assertFalse(g1.equals(null));
    IntegerVectorInitializer nonMultiBound = new IntegerVectorInitializer(2, 2, 4, 0, 8);
    assertFalse(g1.equals(nonMultiBound.createCandidateSolution()));
    IntegerVectorInitializer f3 =
        new IntegerVectorInitializer(
            new int[] {4, 4}, new int[] {5, 5}, new int[] {4, 4}, new int[] {4, 4});
    assertFalse(g1.equals(f3.createCandidateSolution()));
  }

  @Test
  public void testIntegerUnivariate() {
    SingleInteger theClass = new SingleInteger();
    int a = 3;
    int b = 11;
    IntegerValueInitializer f = new IntegerValueInitializer(a, b);
    IntegerValueInitializer fs = f.split();
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      assertEquals(theClass.getClass(), g.getClass());
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
      g.set(a - 1);
      assertEquals(a - 1, g.get());
      g.set(b + 1);
      assertEquals(b + 1, g.get());
    }
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = fs.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      assertEquals(theClass.getClass(), g.getClass());
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
      g.set(a - 1);
      assertEquals(a - 1, g.get());
      g.set(b + 1);
      assertEquals(b + 1, g.get());
    }
    a = -13;
    b = -2;
    f = new IntegerValueInitializer(a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      assertEquals(theClass.getClass(), g.getClass());
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    a = -5;
    b = 5;
    f = new IntegerValueInitializer(a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      assertEquals(theClass.getClass(), g.getClass());
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerValueInitializer(5, 5));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerValueInitializer(5, 5, 2, 6));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerValueInitializer(5, 6, 7, 6));
  }

  @Test
  public void testBoundedIntegerUnivariate() {
    int a = 3;
    int b = 11;
    int min = 0;
    int max = 20;
    IntegerValueInitializer f = new IntegerValueInitializer(a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      g.set(min - 1);
      assertEquals(min, g.get());
      g.set(max + 1);
      assertEquals(max, g.get());
      g.set(10);
      assertEquals(10, g.get());
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a;
    max = b;
    f = new IntegerValueInitializer(a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() < b && g.get() >= a);
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a + 1;
    max = b - 2;
    f = new IntegerValueInitializer(a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      SingleInteger g = f.createCandidateSolution();
      assertTrue(g.get() <= max && g.get() >= min);
      SingleInteger copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
  }

  @Test
  public void testIntegerMultivariate() {
    IntegerVector theClass = new IntegerVector(10);
    int a = 3;
    int b = 11;
    int n = 1;
    IntegerVectorInitializer f = new IntegerVectorInitializer(n, a, b);
    IntegerVectorInitializer fs = f.split();
    assertEquals(f, fs);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertTrue(g.get(0) < b && g.get(0) >= a);
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    a = -13;
    b = -2;
    f = new IntegerVectorInitializer(n, a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertTrue(g.get(0) < b && g.get(0) >= a);
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    a = -5;
    b = 5;
    f = new IntegerVectorInitializer(n, a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertTrue(g.get(0) < b && g.get(0) >= a);
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    n = 10;
    a = 3;
    b = 11;
    f = new IntegerVectorInitializer(n, a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < b && g.get(j) >= a);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    a = -13;
    b = -2;
    f = new IntegerVectorInitializer(n, a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < b && g.get(j) >= a);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    a = -5;
    b = 5;
    f = new IntegerVectorInitializer(n, a, b);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < b && g.get(j) >= a);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    int[] left = {3, -13, -5, 4};
    int[] right = {11, -2, 5, 5};
    n = 4;
    f = new IntegerVectorInitializer(left, right);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(theClass.getClass(), g.getClass());
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
      for (int j = 0; j < n; j++) {
        g.set(j, left[j] - 1);
        assertEquals(left[j] - 1, g.get(j));
        g.set(j, right[j] + 1);
        assertEquals(right[j] + 1, g.get(j));
      }
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerVectorInitializer(1, 5, 5));
    final int[] a1 = {1};
    final int[] a2 = {2, 1};
    final int[] b2 = {2, 3};
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerVectorInitializer(a1, b2));
    thrown =
        assertThrows(IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(1, 5, 5, 2, 6));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(1, 4, 5, 7, 6));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a1, b2, 2, 6));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2, 7, 6));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2, 2, 6));
    final int[] min1 = {1};
    final int[] min2 = {1, 1};
    final int[] max2 = {2, 2};
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a1, b2, min2, max2));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2, min1, max2));
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2, min2, max2));
    a2[0] = 1;
    min2[0] = 3;
    thrown =
        assertThrows(
            IllegalArgumentException.class, () -> new IntegerVectorInitializer(a2, b2, min2, max2));
    thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> new IntegerVectorInitializer(a2, b2, new int[] {1}, new int[] {5}));
  }

  @Test
  public void testBoundedIntegerMultivariate() {
    int a = 4;
    int b = 10;
    int n = 1;
    int min = 2;
    int max = 20;
    IntegerVectorInitializer f = new IntegerVectorInitializer(n, a, b, min, max);
    IntegerVectorInitializer fs = f.split();
    assertEquals(f, fs);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      assertTrue(g.get(0) < b && g.get(0) >= a);
      g.set(0, min - 1);
      assertEquals(min, g.get(0));
      g.set(0, max + 1);
      assertEquals(max, g.get(0));
      g.set(0, 10);
      assertEquals(10, g.get(0));
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a;
    max = b;
    f = new IntegerVectorInitializer(n, a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      assertTrue(g.get(0) < b && g.get(0) >= a);
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a + 1;
    max = b - 2;
    f = new IntegerVectorInitializer(n, a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      assertTrue(g.get(0) <= max && g.get(0) >= min);
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }

    n = 10;
    min = 2;
    max = 20;
    f = new IntegerVectorInitializer(n, a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < b && g.get(j) >= a);
        g.set(j, min - 1);
        assertEquals(min, g.get(j));
        g.set(j, max + 1);
        assertEquals(max, g.get(j));
        g.set(j, 10);
        assertEquals(10, g.get(j));
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a;
    max = b;
    f = new IntegerVectorInitializer(n, a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < b && g.get(j) >= a);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    min = a + 1;
    max = b - 1;
    f = new IntegerVectorInitializer(n, a, b, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) <= max && g.get(j) >= min);
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }

    min = 6;
    max = 15;
    int[] left = {7, 0, 0, 7, 8};
    int[] right = {11, 25, 11, 25, 9};
    n = 5;
    f = new IntegerVectorInitializer(left, right, min, max);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
        assertTrue(g.get(j) <= max && g.get(j) >= min);
        g.set(j, min - 1);
        assertEquals(min, g.get(j));
        g.set(j, max + 1);
        assertEquals(max, g.get(j));
        g.set(j, 10);
        assertEquals(10, g.get(j));
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
    int[] mins = {6, -15, -15, -15, 8};
    int[] maxs = {15, -6, 15, 15, 8};
    left = new int[] {8, -18, 5, -30, 0};
    right = new int[] {12, -10, 18, 30, 20};
    f = new IntegerVectorInitializer(left, right, mins, maxs);
    for (int i = 0; i < NUM_SAMPLES; i++) {
      IntegerVector g = f.createCandidateSolution();
      assertEquals(n, g.length());
      for (int j = 0; j < n; j++) {
        assertTrue(g.get(j) < right[j] && g.get(j) >= left[j]);
        assertTrue(g.get(j) <= maxs[j] && g.get(j) >= mins[j]);
        g.set(j, mins[j] - 1);
        assertEquals(mins[j], g.get(j));
        g.set(j, maxs[j] + 1);
        assertEquals(maxs[j], g.get(j));
        g.set(j, (mins[j] + maxs[j]) / 2);
        assertEquals((mins[j] + maxs[j]) / 2, g.get(j));
      }
      IntegerVector copy = g.copy();
      assertTrue(copy != g);
      assertEquals(g, copy);
      assertEquals(g.getClass(), copy.getClass());
    }
  }
}
