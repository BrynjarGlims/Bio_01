1. Create an initial population of P solutions.
2. Evaluate each solution.
3. Repeat for a fixed number of generations:
    1. Repeat until P offspring solutions are created:
        1. Select two parent solutions in the population (with replacement)
using a randomized selection procedure based on the solution values.
        2. Apply crossover to the two parent solutions to create two offspring
solutions.
        3. Apply mutation (with a small probability) to each offspring.
        4. Include the two offspring in the new population.
    2. Evaluate each offspring in the new population.
    3. Replace the old population by the new one.
4. Return the best solution found.

parent 1:    1234 | 56

parent 2:    1654 | 32

offspring 1: 1234 | 32

offspring 2: 1654 | 56
