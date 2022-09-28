/*
 * Chips-n-Salsa: A library of parallel self-adaptive local search algorithms.
 * Copyright (C) 2002-2022 Vincent A. Cicirello
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
 
package org.cicirello.search.operators.reals;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.cicirello.search.representations.SingleReal;
import org.cicirello.search.representations.RealVector;
import org.cicirello.search.representations.RealValued;

/**
 * JUnit test cases for the classes that implement different variations of
 * Gaussian mutation for mutating floating-point function parameters.
 */
public class GaussianMutationTests extends SharedTestRealMutationOps {
	
	// precision used in floating-point comparisons
	private static final double EPSILON = 1e-10;
	
	@Test
	public void testToArray() {
		GaussianMutation<RealValued> u = GaussianMutation.createGaussianMutation(2);
		double[] a = u.toArray(null);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		a = u.toArray(new double[2]);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		a[0] = 5;
		double[] b = a;
		a = u.toArray(a);
		assertEquals(1, a.length);
		assertEquals(2.0, a[0], EPSILON);
		assertTrue(a==b);
	}
	
	@Test
	public void testExceptions() {
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> GaussianMutation.createGaussianMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> GaussianMutation.createGaussianMutation(1.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableGaussianMutation.createGaussianMutation(1.0, 0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableGaussianMutation.createGaussianMutation(1.0, 0.0)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> GaussianMutation.createGaussianMutation(1.0, 2.0, 2.0 - Math.ulp(2.0))
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> UndoableGaussianMutation.createGaussianMutation(1.0, 2.0, 2.0 - Math.ulp(2.0))
		);
	}
	
	@Test
	public void testGaussianMutation1() {
		GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation();
		assertEquals(1.0, g1.get(0), EPSILON);
		verifyMutate1(g1);
		
		GaussianMutation<RealValued> g5 = GaussianMutation.createGaussianMutation(5.0);
		assertEquals(5.0, g5.get(0), EPSILON);
		verifyMutate1(g5);
		
		UndoableGaussianMutation<RealValued> g1u = UndoableGaussianMutation.createGaussianMutation();
		assertEquals(1.0, g1u.get(0), EPSILON);
		verifyMutate1(g1u);
		verifyUndo(g1u);
		
		UndoableGaussianMutation<RealValued> g5u = UndoableGaussianMutation.createGaussianMutation(5.0);
		assertEquals(5.0, g5u.get(0), EPSILON);
		verifyMutate1(g5u);
		verifyUndo(g5u);
		
		UndoableGaussianMutation<RealValued> g5copyU = g5u.split();
		assertEquals(5.0, g5copyU.get(0), EPSILON);
		assertEquals(g5u.get(0), g5copyU.get(0));
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		
		g5copyU = g5u.copy();
		assertEquals(5.0, g5copyU.get(0), EPSILON);
		assertEquals(g5u.get(0), g5copyU.get(0));
		verifyMutate1(g5copyU);
		verifyUndo(g5copyU);
		
		GaussianMutation<RealValued> g5split = g5.split();
		assertEquals(5.0, g5split.get(0), EPSILON);
		assertEquals(g5.get(0), g5split.get(0));
		assertTrue(g5 != g5split);
		verifyMutate1(g5split);
		
		GaussianMutation<RealValued> g5copyM = g5.copy();
		assertEquals(5.0, g5copyM.get(0), EPSILON);
		assertEquals(g5.get(0), g5copyM.get(0));
		assertTrue(g5 != g5copyM);
		verifyMutate1(g5copyM);
		
		GaussianMutation<RealValued> g3 = g5;
		g3.set(0, 3.0);
		assertEquals(3.0, g3.get(0), EPSILON);
		verifyMutate1(g3);
		
		g3.set(new double[] {7} );
		assertEquals(7.0, g3.get(0));
	}
	
	@Test
	public void testConstrainedGaussianMutation() {
		{
			GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation(1.0, -10.0, 10.0);
			assertEquals(1.0, g1.get(0));
			verifyMutate1(g1);
			
			GaussianMutation<RealValued> g5 = GaussianMutation.createGaussianMutation(5.0, -10.0, 10.0);
			assertEquals(5.0, g5.get(0));
			verifyMutate1(g5);
			
			GaussianMutation<RealValued> g = GaussianMutation.createGaussianMutation(1.0, 2.0, 5.0);
			RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			assertEquals(2.0, r.get(0));
			assertEquals(2.0, r.get(1));
			assertEquals(2.0, r.get(2));
			assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
			assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
			assertEquals(5.0, r.get(5));
			assertEquals(5.0, r.get(6));
			assertEquals(5.0, r.get(7));
			g = GaussianMutation.createGaussianMutation(1.0, 4.0, 4.0);
			r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			for (int i = 0; i < 7; i++) {
				assertEquals(4.0, r.get(i));
			}
		}
		// copy
		{
			GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation(1.0, -10.0, 10.0).copy();
			assertEquals(1.0, g1.get(0));
			verifyMutate1(g1);
			
			GaussianMutation<RealValued> g5 = GaussianMutation.createGaussianMutation(5.0, -10.0, 10.0).copy();
			assertEquals(5.0, g5.get(0));
			verifyMutate1(g5);
			
			GaussianMutation<RealValued> g = GaussianMutation.createGaussianMutation(1.0, 2.0, 5.0).copy();
			RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			assertEquals(2.0, r.get(0));
			assertEquals(2.0, r.get(1));
			assertEquals(2.0, r.get(2));
			assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
			assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
			assertEquals(5.0, r.get(5));
			assertEquals(5.0, r.get(6));
			assertEquals(5.0, r.get(7));
			g = GaussianMutation.createGaussianMutation(1.0, 4.0, 4.0).copy();
			r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			for (int i = 0; i < 7; i++) {
				assertEquals(4.0, r.get(i));
			}
		}
		// split
		{
			GaussianMutation<RealValued> g1 = GaussianMutation.createGaussianMutation(1.0, -10.0, 10.0).split();
			assertEquals(1.0, g1.get(0));
			verifyMutate1(g1);
			
			GaussianMutation<RealValued> g5 = GaussianMutation.createGaussianMutation(5.0, -10.0, 10.0).split();
			assertEquals(5.0, g5.get(0));
			verifyMutate1(g5);
			
			GaussianMutation<RealValued> g = GaussianMutation.createGaussianMutation(1.0, 2.0, 5.0).split();
			RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			assertEquals(2.0, r.get(0));
			assertEquals(2.0, r.get(1));
			assertEquals(2.0, r.get(2));
			assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
			assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
			assertEquals(5.0, r.get(5));
			assertEquals(5.0, r.get(6));
			assertEquals(5.0, r.get(7));
			g = GaussianMutation.createGaussianMutation(1.0, 4.0, 4.0).split();
			r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
			g.mutate(r);
			for (int i = 0; i < 7; i++) {
				assertEquals(4.0, r.get(i));
			}
		}
	}
	
	@Test
	public void testUndoableConstrainedGaussianMutation() {
		UndoableGaussianMutation<RealValued> g1 = UndoableGaussianMutation.createGaussianMutation(1.0, -10.0, 10.0);
		assertEquals(1.0, g1.get(0));
		verifyMutate1(g1);
		verifyUndo(g1);
		
		UndoableGaussianMutation<RealValued> g5 = UndoableGaussianMutation.createGaussianMutation(5.0, -10.0, 10.0);
		assertEquals(5.0, g5.get(0));
		verifyMutate1(g5);
		verifyUndo(g5);
		
		UndoableGaussianMutation<RealValued> g = UndoableGaussianMutation.createGaussianMutation(1.0, 2.0, 5.0);
		RealVector r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
		g.mutate(r);
		assertEquals(2.0, r.get(0));
		assertEquals(2.0, r.get(1));
		assertEquals(2.0, r.get(2));
		assertTrue(r.get(3) >= 2.0 && r.get(3) <= 5.0);
		assertTrue(r.get(4) >= 2.0 && r.get(4) <= 5.0);
		assertEquals(5.0, r.get(5));
		assertEquals(5.0, r.get(6));
		assertEquals(5.0, r.get(7));
		g = UndoableGaussianMutation.createGaussianMutation(1.0, 4.0, 4.0);
		r = new RealVector(new double[] {-1000, -100, -50, 0.0, 3.0, 50, 100, 1000});
		g.mutate(r);
		for (int i = 0; i < 7; i++) {
			assertEquals(4.0, r.get(i));
		}
	}
	
	@Test
	public void testPartialGaussianMutation1() {
		for (int k = 1; k <= 8; k++) {
			UndoableGaussianMutation<RealValued> g1 = UndoableGaussianMutation.createGaussianMutation(1.0, k);
			assertEquals(1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			
			UndoableGaussianMutation<RealValued> g5 = UndoableGaussianMutation.createGaussianMutation(5.0, k);
			assertEquals(5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			
			GaussianMutation<RealValued> g5dis = GaussianMutation.createGaussianMutation(5.0, k);
			assertEquals(5.0, g5dis.get(0), EPSILON);
			assertNotEquals(g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableGaussianMutation<RealValued> g5copy = g5.copy();
			assertEquals(5.0, g5copy.get(0), EPSILON);
			assertEquals(g5.get(0), g5copy.get(0));
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			
			GaussianMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals(5.0, g5copyDis.get(0), EPSILON);
			assertEquals(g5dis.get(0), g5copyDis.get(0));
			verifyMutate1(g5copyDis, k);
			
			UndoableGaussianMutation<RealValued> g5split = g5.split();
			assertEquals(5.0, g5split.get(0), EPSILON);
			assertEquals(g5.get(0), g5split.get(0));
			assertTrue(g5 != g5split);
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			
			GaussianMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals(5.0, g5splitDis.get(0), EPSILON);
			assertEquals(g5dis.get(0), g5splitDis.get(0));
			assertTrue(g5dis != g5splitDis);
			verifyMutate1(g5splitDis, k);
			
			UndoableGaussianMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals(3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
		}
		for (double k = 0.25; k <= 1.1; k+=0.25) {
			UndoableGaussianMutation<RealValued> g1 = UndoableGaussianMutation.createGaussianMutation(1.0, k);
			assertEquals(1.0, g1.get(0), EPSILON);
			verifyMutate1(g1, k);
			verifyUndo(g1);
			
			UndoableGaussianMutation<RealValued> g5 = UndoableGaussianMutation.createGaussianMutation(5.0, k);
			assertEquals(5.0, g5.get(0), EPSILON);
			verifyMutate1(g5, k);
			verifyUndo(g5);
			
			GaussianMutation<RealValued> g5dis = GaussianMutation.createGaussianMutation(5.0, k);
			assertEquals(5.0, g5dis.get(0), EPSILON);
			assertNotEquals(g5, g5dis);
			verifyMutate1(g5dis, k);
			
			UndoableGaussianMutation<RealValued> g5copy = g5.copy();
			assertEquals(5.0, g5copy.get(0), EPSILON);
			assertEquals(g5.get(0), g5copy.get(0));
			verifyMutate1(g5copy, k);
			verifyUndo(g5copy);
			
			GaussianMutation<RealValued> g5copyDis = g5dis.copy();
			assertEquals(5.0, g5copyDis.get(0), EPSILON);
			assertEquals(g5dis.get(0), g5copyDis.get(0));
			verifyMutate1(g5copyDis, k);
			
			UndoableGaussianMutation<RealValued> g5split = g5.split();
			assertEquals(5.0, g5split.get(0), EPSILON);
			assertEquals(g5.get(0), g5split.get(0));
			assertTrue(g5 != g5split);
			verifyMutate1(g5split, k);
			verifyUndo(g5split);
			
			GaussianMutation<RealValued> g5splitDis = g5dis.split();
			assertEquals(5.0, g5splitDis.get(0), EPSILON);
			assertEquals(g5dis.get(0), g5splitDis.get(0));
			assertTrue(g5dis != g5splitDis);
			verifyMutate1(g5splitDis, k);
			
			UndoableGaussianMutation<RealValued> g3 = g5;
			g3.set(0, 3.0);
			assertEquals(3.0, g3.get(0), EPSILON);
			verifyMutate1(g3, k);
			verifyUndo(g3);
		}
	}
}
