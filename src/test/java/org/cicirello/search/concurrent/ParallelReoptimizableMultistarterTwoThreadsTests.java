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

package org.cicirello.search.concurrent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.cicirello.search.ProgressTracker;
import org.cicirello.search.ReoptimizableMetaheuristic;
import org.cicirello.search.SolutionCostPair;
import org.cicirello.search.restarts.ConstantRestartSchedule;
import org.cicirello.search.restarts.ReoptimizableMultistarter;
import org.cicirello.search.restarts.RestartSchedule;
import org.junit.jupiter.api.*;

/** JUnit tests for ParallelReoptimizableMultistarter using 2 threads. */
public class ParallelReoptimizableMultistarterTwoThreadsTests
    extends ParallelMultistarterValidator {

  // optimize tests

  @Test
  public void testConstantLength_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(
                heur, new ConstantRestartSchedule(r), 2);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
        verifyConstantLength(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
        verifyConstantLength(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ReoptimizableMultistarter<TestObject> multiStarter =
            new ReoptimizableMultistarter<TestObject>(heur, r);
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLength_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
            new ArrayList<ReoptimizableMultistarter<TestObject>>();
        TestProblem problem = new TestProblem();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(problem);
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
        TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic(problem);
        heur2.setProgressTracker(heur.getProgressTracker());
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs);
        verifyConstantLength(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testStopped_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 2);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          verifyConstantLengthStopped(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStopped_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heur2.setProgressTracker(heur.getProgressTracker());
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          verifyConstantLengthStopped(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 2);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          verifyConstantLengthBest(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBest_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
          heur2.setProgressTracker(heur.getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          verifyConstantLengthBest(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  // reoptimize tests

  @Test
  public void testConstantLengthRe_Constructor1() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
        verifyConstantLengthRe(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor2() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(
                heur, new ConstantRestartSchedule(r), 2);
        verifyConstantLengthRe(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor3() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
        verifyConstantLengthRe(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor4() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
        schedules.add(new ConstantRestartSchedule(r));
        schedules.add(new ConstantRestartSchedule(r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
        verifyConstantLengthRe(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor5() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
            new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
        TestProblem problem = new TestProblem();
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.add(new TestRestartedMetaheuristic(problem));
        heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
        verifyConstantLengthRe(restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor6() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic();
        ReoptimizableMultistarter<TestObject> multiStarter =
            new ReoptimizableMultistarter<TestObject>(heur, r);
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
        verifyConstantLengthRe(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testConstantLengthRe_Constructor7() {
    for (int r = 1; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
            new ArrayList<ReoptimizableMultistarter<TestObject>>();
        TestProblem problem = new TestProblem();
        TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(problem);
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
        TestRestartedMetaheuristic heur2 = new TestRestartedMetaheuristic(problem);
        heur2.setProgressTracker(heur.getProgressTracker());
        heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
        ParallelReoptimizableMultistarter<TestObject> restarter =
            new ParallelReoptimizableMultistarter<TestObject>(heurs);
        verifyConstantLengthRe(restarter, heur, r, re);
        restarter.close();
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
          verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 2);
          verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthStoppedRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.add(new TestRestartedMetaheuristic(early, early + 1, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          verifyConstantLengthStoppedRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early, early + 1);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
          verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testStoppedRe_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early, early + 1, problem);
          heur2.setProgressTracker(heur.getProgressTracker());
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          verifyConstantLengthStoppedRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor1() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, r, 2);
          verifyConstantLengthBestRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor2() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(
                  heur, new ConstantRestartSchedule(r), 2);
          verifyConstantLengthBestRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor3() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heur, schedules);
          verifyConstantLengthBestRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor4() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ArrayList<RestartSchedule> schedules = new ArrayList<RestartSchedule>();
          schedules.add(new ConstantRestartSchedule(r));
          schedules.add(new ConstantRestartSchedule(r));
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, schedules);
          verifyConstantLengthBestRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor5() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMetaheuristic<TestObject>> heurs =
              new ArrayList<ReoptimizableMetaheuristic<TestObject>>();
          TestProblem problem = new TestProblem();
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.add(new TestRestartedMetaheuristic(early + 1, early, problem));
          heurs.get(1).setProgressTracker(heurs.get(0).getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs, r);
          verifyConstantLengthBestRe(
              restarter, (TestRestartedMetaheuristic) heurs.get(0), r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor6() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          TestRestartedMetaheuristic heur = new TestRestartedMetaheuristic(early + 1, early);
          ReoptimizableMultistarter<TestObject> multiStarter =
              new ReoptimizableMultistarter<TestObject>(heur, r);
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(multiStarter, 2);
          verifyConstantLengthBestRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  @Test
  public void testBestRe_Constructor7() {
    for (int r = 10; r <= 1000; r *= 10) {
      for (int re = 1; re <= 5; re++) {
        int max = re * r;
        for (int early = r - 5, i = 1; early < max; early += r, i++) {
          ArrayList<ReoptimizableMultistarter<TestObject>> heurs =
              new ArrayList<ReoptimizableMultistarter<TestObject>>();
          TestProblem problem = new TestProblem();
          TestRestartedMetaheuristic heur =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur, r));
          TestRestartedMetaheuristic heur2 =
              new TestRestartedMetaheuristic(early + 1, early, problem);
          heurs.add(new ReoptimizableMultistarter<TestObject>(heur2, r));
          heur2.setProgressTracker(heur.getProgressTracker());
          ParallelReoptimizableMultistarter<TestObject> restarter =
              new ParallelReoptimizableMultistarter<TestObject>(heurs);
          verifyConstantLengthBestRe(restarter, heur, r, re, early, i);
          restarter.close();
        }
      }
    }
  }

  private void verifyConstantLength(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(2 * re * r, restarter.getTotalRunLength());
    assertEquals(re, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }

  private void verifyConstantLengthRe(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertEquals(2 * re * r, restarter.getTotalRunLength());
    assertEquals(re, heur.reoptCounter);
    assertEquals(0, heur.optCounter);
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }

  private void verifyConstantLengthStopped(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertTrue(
        2 * early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early,
        "total run length");
    assertTrue(i >= heur.optCounter, "num calls to optimize");
    assertEquals(0, heur.reoptCounter);
    assertFalse(tracker.didFindBest());
    assertTrue(tracker.isStopped());
  }

  private void verifyConstantLengthStoppedRe(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
    assertNotNull(pair);
    assertTrue(pair.getCost() > 1);
    assertTrue(
        2 * early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early,
        "total run length");
    assertTrue(i >= heur.reoptCounter, "num calls to reoptimize");
    assertEquals(0, heur.optCounter);
    assertFalse(tracker.didFindBest());
    assertTrue(tracker.isStopped());
  }

  private void verifyConstantLengthBest(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.optimize(re);
    assertNotNull(pair);
    assertEquals(1, pair.getCost());
    assertTrue(
        2 * early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early,
        "total run length");
    assertTrue(i >= heur.optCounter, "num calls to optimize");
    assertEquals(0, heur.reoptCounter);
    assertTrue(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }

  private void verifyConstantLengthBestRe(
      ParallelReoptimizableMultistarter<TestObject> restarter,
      TestRestartedMetaheuristic heur,
      int r,
      int re,
      int early,
      int i) {
    ProgressTracker<TestObject> tracker = restarter.getProgressTracker();
    assertNotNull(tracker);
    assertEquals(0, restarter.getTotalRunLength());
    assertFalse(tracker.didFindBest());
    assertFalse(tracker.isStopped());
    assertEquals(0, heur.optCounter);
    assertEquals(0, heur.reoptCounter);
    SolutionCostPair<TestObject> pair = restarter.reoptimize(re);
    assertNotNull(pair);
    assertEquals(1, pair.getCost());
    assertTrue(
        2 * early >= restarter.getTotalRunLength() && restarter.getTotalRunLength() >= early,
        "total run length");
    assertTrue(i >= heur.reoptCounter, "num calls to optimize");
    assertEquals(0, heur.optCounter);
    assertTrue(tracker.didFindBest());
    assertFalse(tracker.isStopped());
  }
}
