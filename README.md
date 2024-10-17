# Project 2: Markov

## Introduction

Random Markov processes are widely used in Computer Science and in analyzing different forms of data. This project offers an occasionally amusing look at a *generative model* for creating realistic looking text in a data-driven way. To do so, we will implement two classes: First `WordGram` which represents immutable sequences of words, then `HashMarkov` which will be an efficient model for generating random text that uses `WordGram`s and `HashMap`s.

### `WordGram`

We will implement a class called `WordGram` that represents a sequence of words, represented asan immutable sequence strings.
Example: order-3 `WordGram` objects

<details>
<Summary>Expand to see examples of order-3 `WordGram`s</summary>

| | | |
| --- | --- | --- |
| "cat" | "sleeping" | "nearby" |
| | | |

and 
| | | |
| --- | --- | --- |
| "chocolate" | "doughnuts" | "explode" |
| | | |

</details> 

### Markov Model

We want to create a Markov model for generating random text that looks similar to a training text. We will generate one random word at a time, where probabilities for the next word are based on the previous words. An order-k Markov model uses order-k `WordGram`s to predict text. To begin, we select a random k-gram from the *training text* (the data we use to create our model; we want to generate random text similar to the training text). Then, we look for instances of that k-gram in the training text in order to calculate the probabilities corresponding to words that might follow. We then generate a new word according to these probabilities, after which we repeat the process using the last k-1 words from the previous k-gram and the newly generated word.

Example: suppose we are using an order 2 Markov model with the following training text (located in `testfile.txt`):

```
this is a test
it is only a test
do you think it is a test
this test it is ok
it is short but it is ok to be short
```

We begin with a random k-gram, suppose we get `[it, is]`. This appears 5 times in total, and is followed by `only`, `a`, `ok`, `short`, and again by `ok` each of those five times respectively. So the probability (in the training text) that `it is` is followed by `ok` is 2/5 or 40%, and for the other words is 1/5 or 20%. To generate a random word following the 2-gram `[it, is]`, we would therefore choose `ok` with 2/5 probability, or `only`, `a`, or `short` with 1/5 probability each.

Rather than calculating these probabilities explicitly, our code will use them implicitly. In particular, the `getFollows` method will return a `List` of *all* of the words that follow after a given k-gram in the training text (including duplicates), and then we will choose one of these words uniformly at random. Words that more commonly follow will be selected with higher probability by virtue of being duplicated in the `List`. Suppose we choose `ok` as the next random word. Then the random text generated so far is `it is ok`, and the current `WordGram` of order 2 we are using would be updated to `[is, ok]`. We then again find the following words in the training text, and so on and so forth, until we have generated the desired number of random words.

## Running Driver Code

<details><summary>Expand for details on `MarkovDriver`</summary>

- Some static variables used in the main method are defined at the top of class, namely:
  - `TEXT_SIZE` is the number of words to be randomly generated.
  - `RANDOM_SEED` is the random seed used to initialize the random number generator. We should always get the same random text given a particular random seed and training text.
  - `MODEL_ORDER` is the order of `WordGram`s that will be used.
  - `PRINT_MODE` can be set to true or false based on whether you want the random text generated to be printed.
- The `filename` defined at the beginning of the main method determines the file that will be used for the training text. By default it is set to `data/alice.txt`, meaning the text of *Alice in Wonderland* is being used. Note that data files are located inside the data folder.
- A `MarkovInterface` object named `generator` is created. By default, it uses `BaseMarkov` as the implementing class, a complete implementation of which is provided in the starter code.
- The `generator` sets the specified random seed.
- The `generator` is timed in how long it takes to run two methods: first `setTraining()` and then `getRandomText()`.
- Finally, values are printed: The random text itself if `PRINT_MODE` is set to true and the time it took to train (that is, for `setTraining()` to run) the Markov model and to generate random text using the model (that is, for `getRandomText` to run). 

</details>

## JUnit Tests

To test our `WordGram` and `HashMarkov` implementations, *unit tests* in `WordGramTest.java` and `MarkovTest.java` are used.

<details>
<summary>Expand here for screenshot of getModel in MarkovTest</summary>

<div align="center">
  <img width="400" height="200" src="figures/markovTest.png">
</div>

</details>


## Part 1: Developing the `WordGram` Class

`WordGram` objects are *immutable*, meaning they should not change after creation (similar to Java Strings). Therefore, none of the methods except the constructor should *mutate* (or change) the words associated with a `WordGram` object.

Details about individual methods:

<details>
<summary>Expand for details on the Constructor</summary>

A WordGram` object is constructed by passing as constructor arguments: an array, a starting index, and the size (or order) of the `WordGram.` The strings are stored in an array instance variable by copying them from the array passed to the constructor.

Instance variables in `WordGram`:
```
private String[] myWords;
private String myToString;
private int myHash;
```

The constructor for WordGram `public WordGram(String[] source, int start, int size)`
stores `size` strings from the array `source`, starting at index `start` (of `source`) into the private `String` array instance variable `myWords` of the `WordGram` class. The array `myWords` should contain exactly `size` strings. 

For example, suppose parameter `words` is the array below, with "this" at index 0.

| | | | | | | |
| --- | --- | --- | --- | --- | --- | --- |
| "this" | "is" | "a" | "test" |"of" |"the" |"code" |
| | | | | | |

The call `new WordGram(words,3,4)` should create this array `myWords` since the starting index is the second parameter, 3, and the size is the third parameter, 4.

| | | | |
| --- | --- | --- | --- |
| "test" | "of" | "the" | "code"|
| | | | |

</details>

<details>
<summary>Expand for details on wordAt()</summary>

The `wordAt()` method returns the word at the given index in `myWords`. 

</details>

<details>
<summary>Expand for details on length()</summary>

The `length()` method should return the order of the `WordGram`, that is, the length of `myWords`. 
</details>

<details>
<summary>Expand for details on equals()</summary>

The `equals()` method should return `true` when the parameter passed is a `WordGram` object with **the same strings in the same order** as this object. 

The strings in the array `myWords` of `other` and `this` (the object on which `equals()` is called) are then compared. The code checks that `WordGram` objects of different lengths are not equal.

</details>

<details>
<summary>Expand for details on hashCode()</summary>

The `hashCode()` method should return an `int` value based on all the strings in instance variable `myWords`. Since `WordGram` objects are immutable (do not change after creation), we can compute the hash value the first time `.hashCode()` is called (which you can check against whatever default value you might set in the constructor), and store the result in the `myHash` instance variable. On subsequent calls, we simply return `myHash`.
</details>

<details>
<summary>Expand for details on shiftAdd()</summary>

If this `WordGram` has k entries then the `shiftAdd()` method should create and return a _**new**_ `WordGram` object, also with k entries, whose *first* k-1 entries are the same as the *last* k-1 entries of this `WordGram`, and whose last entry is the parameter `last`. **This method creates a new WordGram object** and copy values correctly to return back to the user.

For example, if `WordGram w` is 
| | | |
| --- | --- | --- |
| "apple" | "pear" | "cherry" |
| | | | 

then the method call `w.shiftAdd("lemon")` returns a new `WordGram` containing {"pear", "cherry", "lemon"}. This new `WordGram` will not equal w.

</details>

<details>
<summary>Expand for details on toString()</summary>

The `toString()` method should return a printable `String` representing all the strings stored in the `WordGram` instance variable `myWords`, each separated by a single blank space (that is, `" "`). 

Instead of recomputing this `String` each time `toString()` is called, we store the String in instance variable `myToString`. On subsequent calls our code should simply return the value stored in `myToString` (again using the immutability of `WordGram`s to ensure this value will not change). To determine whether a given call to `toString()` is the first, we can compare to the default constructor value of `myToString`.

</details>

After implementing the `WordGram` class, we can run the `WordGramTest` [JUnit tests](#junit-tests).

<details><summary>Expand for example output of MarkovDriver with correct WordGram</summary>

```
Trained on text in data/alice.txt with T=28196 words
Training time = 0.012 s
Generated N=100 random words with order 2 Markov Model
Generating time = 0.060 s
----------------------------------
Alice; `I daresay it's a set of verses.' `Are they in the distance, and she swam 
about, trying to touch her. `Poor little thing!' said Alice, `a great girl like you,' 
(she might well say this), `to go on with the Dutchess, it had made. `He took me 
for a few minutes to see a little worried. `Just about as it turned a corner, `Oh 
my ears and whiskers, how late it's getting!' She was close behind it was growing, 
and very neatly and simply arranged; the only one who had got its head to keep back 
the wandering hair
```

</details>

## Part 2: Developing the HashMarkov Class

This code develops a Markov model for generating random text using `WordGram`s and hashing. We created a new `HashMarkov.java` file with a single public `HashMarkov` class that implements the `MarkovInterface`. 

Our implementation has the same behavior as `BaseMarkov` in terms of implementing the interface methods and generating the same output, but has different performance properties due to the use of a `HashMap` in training. In particular, `HashMarkov` creates an instance variable `HashMap` that maps `WordGram`s of a given order to `List`s of words that follow that `WordGram`. The training text should be read (looped over) *exactly once* during the `setTraining()` method to create this map. Subsequently, the `getFollows()` method should simply return the corresponding `List` from the map, or an empty `List` if there is no entry in the map, and this should be used in `getRandomText()` to avoid having to search the training text again for every random word generated.

<details>
<summary>Expand for details on instance variables</summary>

The same instance variables are used from `BaseMarkov` for storing the words of the training text, the random number generator, and the order of the model. In addition, we need a `HashMap` instance variable that maps from `WordGram`s (the keys) to `List<String>` (the values). 

</details>

<details>
<summary>Expand for details on Constructor</summary>

At least one constructor takes as input the order of `WordGram`s used in the model. This initializes the instance variables.

</details>

<details>
<summary>Expand for details on setTraining()</summary>

`setTraining()` method stores the words of the training text in an Array of Strings. We use the method call `text.split("\\s+")` to do so. In addition, we clear the `HashMap` instance variable to ensure that the map does not contain stale data if `setTraining()` is called multiple times on different training texts. Finally, we loop through the words in the training text *exactly once* and, for each `WordGram` of the given order in the text, add all of the words that follow it to the corresponding `List<String>` value in your `HashMap` instance variable.

</details>

<details>
<summary>Expand for details on getFollows()</summary>

The `getFollows` method takes a `WordGram` object `wgram` as a parameter and returns a `List` of all the words (represented as `String`s) that follow from `wgram` in the training text. The `HashMarkov` implementation should be more efficient, as it should *not* loop over the training text, but should instead simply lookup the `List` in the `HashMap` instance variable intialized during `setTraining()`, or return an empty `List` if the `wgram` is not a key in the map.

</details>

<details>
<summary>Expand for details on getRandomText()</summary>

This method uses the `HashMap` instance variable set during `setTraining()` and the `getFollows()` method to generate `length` words of random text one at a time. In order to adhere to the specification that `HashMarkov` should generate the same random text as `BaseMarkov` given the same random seed, **we need to use the random number generator in the same way as `BaseMarkov`.** In particular:
- Make one call to `nextInt()` at the beginning to get the initial random `WordGram`,
- Make one call to `nextInt()` at every iteration of the main text generating loop. Either you need to a random word from a the `getFollows` list, or you need to get a random word from the entire text. See the `getNext` method of `BaseMarkov` for an example.

Unlike `BaseMarkov`, our implementation does *not* loop over the words of the training text again every time it generates a next word.

</details>

<details>
<summary>Expand for details on getOrder() and setSeed()</summary>

`getOrder()` is a getter method that returns the order of the Markov model, stored in an instance variable. `setSeed()` calls the `setSeed()` method of the random number generator instance variable and pass the corresponding random seed.

</details>

Coursework from Duke CS 201: Data Structures and Algorithms.
