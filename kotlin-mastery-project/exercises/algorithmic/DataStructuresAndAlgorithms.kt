/**
 * Data Structures and Algorithms in Kotlin
 * 
 * This module provides comprehensive implementations of common data structures
 * and algorithms in Kotlin, focusing on:
 * - Custom data structure implementations
 * - Algorithm design patterns
 * - Time and space complexity analysis
 * - Practical problem-solving techniques
 * - Performance optimization strategies
 * - Real-world application examples
 */

import kotlin.math.*
import kotlin.random.Random

// ================================
// Linear Data Structures
// ================================

/**
 * Dynamic Array Implementation
 */
class DynamicArray<T> : Iterable<T> {
    private var array: Array<Any?> = arrayOfNulls(16)
    private var size = 0
    
    val capacity: Int get() = array.size
    val isEmpty: Boolean get() = size == 0
    val lastIndex: Int get() = size - 1
    
    fun add(element: T) {
        ensureCapacity()
        array[size++] = element
    }
    
    fun add(index: Int, element: T) {
        if (index < 0 || index > size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        ensureCapacity()
        
        // Shift elements to the right
        for (i in size downTo index + 1) {
            array[i] = array[i - 1]
        }
        array[index] = element
        size++
    }
    
    @Suppress("UNCHECKED_CAST")
    fun get(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        return array[index] as T
    }
    
    fun set(index: Int, element: T): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        @Suppress("UNCHECKED_CAST")
        val oldValue = array[index] as T
        array[index] = element
        return oldValue
    }
    
    fun removeAt(index: Int): T {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException("Index: $index, Size: $size")
        
        @Suppress("UNCHECKED_CAST")
        val oldValue = array[index] as T
        
        // Shift elements to the left
        for (i in index until size - 1) {
            array[i] = array[i + 1]
        }
        array[--size] = null
        
        return oldValue
    }
    
    fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index != -1) {
            removeAt(index)
            true
        } else {
            false
        }
    }
    
    fun indexOf(element: T): Int {
        for (i in 0 until size) {
            if (array[i] == element) return i
        }
        return -1
    }
    
    fun contains(element: T): Boolean = indexOf(element) != -1
    
    fun clear() {
        for (i in 0 until size) {
            array[i] = null
        }
        size = 0
    }
    
    private fun ensureCapacity() {
        if (size == array.size) {
            val newArray = arrayOfNulls<Any?>(array.size * 2)
            Array.copy(array, 0, newArray, 0, size)
            array = newArray
        }
    }
    
    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var index = 0
            
            override fun hasNext(): Boolean = index < size
            
            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()
                return get(index++)
            }
        }
    }
    
    fun size(): Int = size
    
    override fun toString(): String {
        return (0 until size).joinToString(", ", "[", "]") { get(it).toString() }
    }
}

/**
 * Linked List Implementation
 */
class LinkedList<T> : Iterable<T> {
    private var head: Node<T>? = null
    private var tail: Node<T>? = null
    private var size = 0
    
    private data class Node<T>(
        val data: T,
        var next: Node<T>? = null,
        var prev: Node<T>? = null
    )
    
    val isEmpty: Boolean get() = size == 0
    val first: T? get() = head?.data
    val last: T? get() = tail?.data
    
    fun addFirst(element: T) {
        val newNode = Node(element, next = head)
        head?.prev = newNode
        head = newNode
        
        if (tail == null) tail = newNode
        size++
    }
    
    fun addLast(element: T) {
        val newNode = Node(element, prev = tail)
        tail?.next = newNode
        tail = newNode
        
        if (head == null) head = newNode
        size++
    }
    
    fun add(element: T) = addLast(element)
    
    fun removeFirst(): T {
        if (head == null) throw NoSuchElementException("List is empty")
        
        val data = head!!.data
        head = head!!.next
        head?.prev = null
        
        if (head == null) tail = null
        size--
        return data
    }
    
    fun removeLast(): T {
        if (tail == null) throw NoSuchElementException("List is empty")
        
        val data = tail!!.data
        tail = tail!!.prev
        tail?.next = null
        
        if (tail == null) head = null
        size--
        return data
    }
    
    fun contains(element: T): Boolean {
        var current = head
        while (current != null) {
            if (current.data == element) return true
            current = current.next
        }
        return false
    }
    
    fun remove(element: T): Boolean {
        var current = head
        while (current != null) {
            if (current.data == element) {
                // Remove current node
                current.prev?.next = current.next
                current.next?.prev = current.prev
                
                if (current == head) head = current.next
                if (current == tail) tail = current.prev
                
                size--
                return true
            }
            current = current.next
        }
        return false
    }
    
    fun clear() {
        head = null
        tail = null
        size = 0
    }
    
    fun size(): Int = size
    
    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            private var current = head
            
            override fun hasNext(): Boolean = current != null
            
            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()
                val data = current!!.data
                current = current!!.next
                return data
            }
        }
    }
    
    override fun toString(): String {
        return joinToString(", ", "[", "]")
    }
}

/**
 * Stack Implementation
 */
class Stack<T> {
    private val list = LinkedList<T>()
    
    val isEmpty: Boolean get() = list.isEmpty
    val size: Int get() = list.size()
    
    fun push(element: T) {
        list.addFirst(element)
    }
    
    fun pop(): T {
        if (isEmpty) throw IllegalStateException("Stack is empty")
        return list.removeFirst()
    }
    
    fun peek(): T {
        if (isEmpty) throw IllegalStateException("Stack is empty")
        return list.first!!
    }
    
    fun clear() = list.clear()
    
    override fun toString(): String = list.toString()
}

/**
 * Queue Implementation
 */
class Queue<T> {
    private val list = LinkedList<T>()
    
    val isEmpty: Boolean get() = list.isEmpty
    val size: Int get() = list.size()
    
    fun enqueue(element: T) {
        list.addLast(element)
    }
    
    fun dequeue(): T {
        if (isEmpty) throw IllegalStateException("Queue is empty")
        return list.removeFirst()
    }
    
    fun front(): T {
        if (isEmpty) throw IllegalStateException("Queue is empty")
        return list.first!!
    }
    
    fun clear() = list.clear()
    
    override fun toString(): String = list.toString()
}

// ================================
// Tree Data Structures
// ================================

/**
 * Binary Search Tree Implementation
 */
class BinarySearchTree<T : Comparable<T>> {
    private var root: TreeNode<T>? = null
    private var size = 0
    
    private data class TreeNode<T>(
        val data: T,
        var left: TreeNode<T>? = null,
        var right: TreeNode<T>? = null
    )
    
    val isEmpty: Boolean get() = root == null
    
    fun insert(element: T) {
        root = insertRecursive(root, element)
        size++
    }
    
    private fun insertRecursive(node: TreeNode<T>?, element: T): TreeNode<T> {
        if (node == null) return TreeNode(element)
        
        when {
            element < node.data -> node.left = insertRecursive(node.left, element)
            element > node.data -> node.right = insertRecursive(node.right, element)
            // Equal elements are not inserted again
        }
        return node
    }
    
    fun contains(element: T): Boolean {
        return containsRecursive(root, element)
    }
    
    private fun containsRecursive(node: TreeNode<T>?, element: T): Boolean {
        if (node == null) return false
        
        return when {
            element < node.data -> containsRecursive(node.left, element)
            element > node.data -> containsRecursive(node.right, element)
            else -> true
        }
    }
    
    fun remove(element: T): Boolean {
        val initialSize = size
        root = removeRecursive(root, element)
        return size < initialSize
    }
    
    private fun removeRecursive(node: TreeNode<T>?, element: T): TreeNode<T>? {
        if (node == null) return null
        
        when {
            element < node.data -> node.left = removeRecursive(node.left, element)
            element > node.data -> node.right = removeRecursive(node.right, element)
            else -> {
                // Node to be deleted found
                size--
                
                // Case 1: No children
                if (node.left == null && node.right == null) return null
                
                // Case 2: One child
                if (node.left == null) return node.right
                if (node.right == null) return node.left
                
                // Case 3: Two children
                val minRight = findMin(node.right!!)
                return TreeNode(minRight.data, node.left, removeRecursive(node.right, minRight.data))
            }
        }
        return node
    }
    
    private fun findMin(node: TreeNode<T>): TreeNode<T> {
        return if (node.left == null) node else findMin(node.left!!)
    }
    
    fun inOrderTraversal(): List<T> {
        val result = mutableListOf<T>()
        inOrderRecursive(root, result)
        return result
    }
    
    private fun inOrderRecursive(node: TreeNode<T>?, result: MutableList<T>) {
        if (node != null) {
            inOrderRecursive(node.left, result)
            result.add(node.data)
            inOrderRecursive(node.right, result)
        }
    }
    
    fun preOrderTraversal(): List<T> {
        val result = mutableListOf<T>()
        preOrderRecursive(root, result)
        return result
    }
    
    private fun preOrderRecursive(node: TreeNode<T>?, result: MutableList<T>) {
        if (node != null) {
            result.add(node.data)
            preOrderRecursive(node.left, result)
            preOrderRecursive(node.right, result)
        }
    }
    
    fun postOrderTraversal(): List<T> {
        val result = mutableListOf<T>()
        postOrderRecursive(root, result)
        return result
    }
    
    private fun postOrderRecursive(node: TreeNode<T>?, result: MutableList<T>) {
        if (node != null) {
            postOrderRecursive(node.left, result)
            postOrderRecursive(node.right, result)
            result.add(node.data)
        }
    }
    
    fun height(): Int {
        return heightRecursive(root)
    }
    
    private fun heightRecursive(node: TreeNode<T>?): Int {
        if (node == null) return -1
        return 1 + maxOf(heightRecursive(node.left), heightRecursive(node.right))
    }
    
    fun size(): Int = size
}

/**
 * Hash Table Implementation (Separate Chaining)
 */
class HashTable<K, V> {
    private var buckets: Array<MutableList<Entry<K, V>>?> = arrayOfNulls(16)
    private var size = 0
    private var threshold = (buckets.size * 0.75).toInt()
    
    private data class Entry<K, V>(val key: K, var value: V)
    
    val isEmpty: Boolean get() = size == 0
    
    fun put(key: K, value: V): V? {
        if (size >= threshold) resize()
        
        val index = getIndex(key)
        val bucket = buckets[index] ?: run {
            val newBucket = mutableListOf<Entry<K, V>>()
            buckets[index] = newBucket
            newBucket
        }
        
        // Check if key already exists
        bucket.find { it.key == key }?.let { entry ->
            val oldValue = entry.value
            entry.value = value
            return oldValue
        }
        
        // Add new entry
        bucket.add(Entry(key, value))
        size++
        return null
    }
    
    fun get(key: K): V? {
        val index = getIndex(key)
        val bucket = buckets[index] ?: return null
        return bucket.find { it.key == key }?.value
    }
    
    fun remove(key: K): V? {
        val index = getIndex(key)
        val bucket = buckets[index] ?: return null
        
        val iterator = bucket.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                iterator.remove()
                size--
                return entry.value
            }
        }
        return null
    }
    
    fun containsKey(key: K): Boolean = get(key) != null
    
    fun keys(): Set<K> {
        val result = mutableSetOf<K>()
        buckets.filterNotNull().forEach { bucket ->
            bucket.forEach { entry ->
                result.add(entry.key)
            }
        }
        return result
    }
    
    fun values(): Collection<V> {
        val result = mutableListOf<V>()
        buckets.filterNotNull().forEach { bucket ->
            bucket.forEach { entry ->
                result.add(entry.value)
            }
        }
        return result
    }
    
    private fun getIndex(key: K): Int {
        return key.hashCode().absoluteValue % buckets.size
    }
    
    private fun resize() {
        val oldBuckets = buckets
        buckets = arrayOfNulls(buckets.size * 2)
        threshold = (buckets.size * 0.75).toInt()
        size = 0
        
        oldBuckets.filterNotNull().forEach { bucket ->
            bucket.forEach { entry ->
                put(entry.key, entry.value)
            }
        }
    }
    
    fun size(): Int = size
    
    fun loadFactor(): Double = size.toDouble() / buckets.size
}

// ================================
// Sorting Algorithms
// ================================

object SortingAlgorithms {
    
    /**
     * Quick Sort Implementation
     * Time Complexity: O(n log n) average, O(n²) worst case
     * Space Complexity: O(log n)
     */
    fun <T : Comparable<T>> quickSort(array: MutableList<T>, low: Int = 0, high: Int = array.size - 1) {
        if (low < high) {
            val partitionIndex = partition(array, low, high)
            quickSort(array, low, partitionIndex - 1)
            quickSort(array, partitionIndex + 1, high)
        }
    }
    
    private fun <T : Comparable<T>> partition(array: MutableList<T>, low: Int, high: Int): Int {
        val pivot = array[high]
        var i = low - 1
        
        for (j in low until high) {
            if (array[j] <= pivot) {
                i++
                array.swap(i, j)
            }
        }
        array.swap(i + 1, high)
        return i + 1
    }
    
    /**
     * Merge Sort Implementation  
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     */
    fun <T : Comparable<T>> mergeSort(array: MutableList<T>) {
        if (array.size > 1) {
            val mid = array.size / 2
            val left = array.subList(0, mid).toMutableList()
            val right = array.subList(mid, array.size).toMutableList()
            
            mergeSort(left)
            mergeSort(right)
            
            merge(array, left, right)
        }
    }
    
    private fun <T : Comparable<T>> merge(array: MutableList<T>, left: List<T>, right: List<T>) {
        var i = 0
        var j = 0
        var k = 0
        
        while (i < left.size && j < right.size) {
            if (left[i] <= right[j]) {
                array[k] = left[i]
                i++
            } else {
                array[k] = right[j]
                j++
            }
            k++
        }
        
        while (i < left.size) {
            array[k] = left[i]
            i++
            k++
        }
        
        while (j < right.size) {
            array[k] = right[j]
            j++
            k++
        }
    }
    
    /**
     * Heap Sort Implementation
     * Time Complexity: O(n log n)
     * Space Complexity: O(1)
     */
    fun <T : Comparable<T>> heapSort(array: MutableList<T>) {
        val n = array.size
        
        // Build max heap
        for (i in n / 2 - 1 downTo 0) {
            heapify(array, n, i)
        }
        
        // Extract elements from heap one by one
        for (i in n - 1 downTo 0) {
            array.swap(0, i)
            heapify(array, i, 0)
        }
    }
    
    private fun <T : Comparable<T>> heapify(array: MutableList<T>, n: Int, i: Int) {
        var largest = i
        val left = 2 * i + 1
        val right = 2 * i + 2
        
        if (left < n && array[left] > array[largest]) {
            largest = left
        }
        
        if (right < n && array[right] > array[largest]) {
            largest = right
        }
        
        if (largest != i) {
            array.swap(i, largest)
            heapify(array, n, largest)
        }
    }
    
    /**
     * Insertion Sort Implementation
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * Good for small arrays or nearly sorted arrays
     */
    fun <T : Comparable<T>> insertionSort(array: MutableList<T>) {
        for (i in 1 until array.size) {
            val key = array[i]
            var j = i - 1
            
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j]
                j--
            }
            array[j + 1] = key
        }
    }
    
    private fun <T> MutableList<T>.swap(i: Int, j: Int) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }
}

// ================================
// Search Algorithms
// ================================

object SearchAlgorithms {
    
    /**
     * Binary Search Implementation
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * Requires sorted array
     */
    fun <T : Comparable<T>> binarySearch(array: List<T>, target: T): Int {
        var left = 0
        var right = array.size - 1
        
        while (left <= right) {
            val mid = left + (right - left) / 2
            
            when {
                array[mid] == target -> return mid
                array[mid] < target -> left = mid + 1
                else -> right = mid - 1
            }
        }
        
        return -1 // Not found
    }
    
    /**
     * Linear Search Implementation
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    fun <T> linearSearch(array: List<T>, target: T): Int {
        for (i in array.indices) {
            if (array[i] == target) return i
        }
        return -1 // Not found
    }
    
    /**
     * Depth-First Search for Graph/Tree
     */
    fun <T> depthFirstSearch(
        start: T,
        target: T,
        getNeighbors: (T) -> List<T>
    ): List<T>? {
        val visited = mutableSetOf<T>()
        val path = mutableListOf<T>()
        
        fun dfsRecursive(current: T): Boolean {
            if (current in visited) return false
            
            visited.add(current)
            path.add(current)
            
            if (current == target) return true
            
            for (neighbor in getNeighbors(current)) {
                if (dfsRecursive(neighbor)) return true
            }
            
            path.removeAt(path.size - 1)
            return false
        }
        
        return if (dfsRecursive(start)) path else null
    }
    
    /**
     * Breadth-First Search for Graph/Tree
     */
    fun <T> breadthFirstSearch(
        start: T,
        target: T,
        getNeighbors: (T) -> List<T>
    ): List<T>? {
        val visited = mutableSetOf<T>()
        val queue = Queue<T>()
        val parent = mutableMapOf<T, T>()
        
        queue.enqueue(start)
        visited.add(start)
        
        while (!queue.isEmpty) {
            val current = queue.dequeue()
            
            if (current == target) {
                // Reconstruct path
                val path = mutableListOf<T>()
                var node = current
                while (true) {
                    path.add(0, node)
                    val p = parent[node]
                    if (p == null) break
                    node = p
                }
                return path
            }
            
            for (neighbor in getNeighbors(current)) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    parent[neighbor] = current
                    queue.enqueue(neighbor)
                }
            }
        }
        
        return null // Path not found
    }
}

// ================================
// Dynamic Programming Examples
// ================================

object DynamicProgramming {
    
    /**
     * Fibonacci with Memoization
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    fun fibonacci(n: Int): Long {
        val memo = mutableMapOf<Int, Long>()
        
        fun fibHelper(n: Int): Long {
            if (n <= 1) return n.toLong()
            
            return memo.getOrPut(n) {
                fibHelper(n - 1) + fibHelper(n - 2)
            }
        }
        
        return fibHelper(n)
    }
    
    /**
     * Longest Common Subsequence
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     */
    fun longestCommonSubsequence(text1: String, text2: String): Int {
        val m = text1.length
        val n = text2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (text1[i - 1] == text2[j - 1]) {
                    dp[i - 1][j - 1] + 1
                } else {
                    maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }
        
        return dp[m][n]
    }
    
    /**
     * 0/1 Knapsack Problem
     * Time Complexity: O(n * W)
     * Space Complexity: O(n * W)
     */
    fun knapsack(weights: IntArray, values: IntArray, capacity: Int): Int {
        val n = weights.size
        val dp = Array(n + 1) { IntArray(capacity + 1) }
        
        for (i in 1..n) {
            for (w in 1..capacity) {
                dp[i][w] = if (weights[i - 1] <= w) {
                    maxOf(
                        dp[i - 1][w],
                        dp[i - 1][w - weights[i - 1]] + values[i - 1]
                    )
                } else {
                    dp[i - 1][w]
                }
            }
        }
        
        return dp[n][capacity]
    }
    
    /**
     * Coin Change Problem
     * Time Complexity: O(amount * n)
     * Space Complexity: O(amount)
     */
    fun coinChange(coins: IntArray, amount: Int): Int {
        val dp = IntArray(amount + 1) { amount + 1 }
        dp[0] = 0
        
        for (i in 1..amount) {
            for (coin in coins) {
                if (coin <= i) {
                    dp[i] = minOf(dp[i], dp[i - coin] + 1)
                }
            }
        }
        
        return if (dp[amount] > amount) -1 else dp[amount]
    }
}

// ================================
// Problem-Solving Examples
// ================================

object ProblemSolving {
    
    /**
     * Two Sum Problem
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    fun twoSum(numbers: IntArray, target: Int): Pair<Int, Int>? {
        val seen = mutableMapOf<Int, Int>()
        
        for (i in numbers.indices) {
            val complement = target - numbers[i]
            if (complement in seen) {
                return seen[complement]!! to i
            }
            seen[numbers[i]] = i
        }
        
        return null
    }
    
    /**
     * Valid Parentheses
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    fun isValidParentheses(s: String): Boolean {
        val stack = Stack<Char>()
        val pairs = mapOf(')' to '(', '}' to '{', ']' to '[')
        
        for (char in s) {
            when (char) {
                '(', '{', '[' -> stack.push(char)
                ')', '}', ']' -> {
                    if (stack.isEmpty || stack.pop() != pairs[char]) {
                        return false
                    }
                }
            }
        }
        
        return stack.isEmpty
    }
    
    /**
     * Maximum Subarray Sum (Kadane's Algorithm)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    fun maxSubarraySum(nums: IntArray): Int {
        var maxSoFar = nums[0]
        var maxEndingHere = nums[0]
        
        for (i in 1 until nums.size) {
            maxEndingHere = maxOf(nums[i], maxEndingHere + nums[i])
            maxSoFar = maxOf(maxSoFar, maxEndingHere)
        }
        
        return maxSoFar
    }
    
    /**
     * Reverse Linked List
     */
    fun <T> reverseLinkedList(list: LinkedList<T>): LinkedList<T> {
        val reversed = LinkedList<T>()
        val stack = Stack<T>()
        
        // Push all elements to stack
        for (element in list) {
            stack.push(element)
        }
        
        // Pop all elements and add to new list
        while (!stack.isEmpty) {
            reversed.add(stack.pop())
        }
        
        return reversed
    }
    
    /**
     * Find Duplicate Number
     * Time Complexity: O(n)
     * Space Complexity: O(1) using Floyd's Cycle Detection
     */
    fun findDuplicate(nums: IntArray): Int {
        // Phase 1: Find intersection point in the cycle
        var slow = nums[0]
        var fast = nums[0]
        
        do {
            slow = nums[slow]
            fast = nums[nums[fast]]
        } while (slow != fast)
        
        // Phase 2: Find the entrance to the cycle
        slow = nums[0]
        while (slow != fast) {
            slow = nums[slow]
            fast = nums[fast]
        }
        
        return slow
    }
}

// ================================
// Performance Testing and Benchmarks
// ================================

object AlgorithmBenchmark {
    
    fun benchmarkSortingAlgorithms(sizes: List<Int>) {
        println("=== Sorting Algorithms Benchmark ===")
        
        for (size in sizes) {
            println("\nArray Size: $size")
            
            val testData = generateRandomArray(size)
            
            // Quick Sort
            val quickSortData = testData.toMutableList()
            val quickSortTime = measureTimeMillis {
                SortingAlgorithms.quickSort(quickSortData)
            }
            println("Quick Sort: ${quickSortTime}ms")
            
            // Merge Sort
            val mergeSortData = testData.toMutableList()
            val mergeSortTime = measureTimeMillis {
                SortingAlgorithms.mergeSort(mergeSortData)
            }
            println("Merge Sort: ${mergeSortTime}ms")
            
            // Heap Sort
            val heapSortData = testData.toMutableList()
            val heapSortTime = measureTimeMillis {
                SortingAlgorithms.heapSort(heapSortData)
            }
            println("Heap Sort: ${heapSortTime}ms")
            
            // Insertion Sort (only for smaller arrays)
            if (size <= 10000) {
                val insertionSortData = testData.toMutableList()
                val insertionSortTime = measureTimeMillis {
                    SortingAlgorithms.insertionSort(insertionSortData)
                }
                println("Insertion Sort: ${insertionSortTime}ms")
            }
        }
    }
    
    fun benchmarkSearchAlgorithms() {
        println("\n=== Search Algorithms Benchmark ===")
        
        val sizes = listOf(1000, 10000, 100000, 1000000)
        
        for (size in sizes) {
            println("\nArray Size: $size")
            
            val sortedData = (1..size).toList()
            val target = Random.nextInt(1, size + 1)
            
            // Binary Search
            val binarySearchTime = measureTimeMillis {
                repeat(1000) {
                    SearchAlgorithms.binarySearch(sortedData, target)
                }
            }
            println("Binary Search (1000 iterations): ${binarySearchTime}ms")
            
            // Linear Search
            if (size <= 100000) { // Only for smaller arrays
                val linearSearchTime = measureTimeMillis {
                    repeat(1000) {
                        SearchAlgorithms.linearSearch(sortedData, target)
                    }
                }
                println("Linear Search (1000 iterations): ${linearSearchTime}ms")
            }
        }
    }
    
    private fun generateRandomArray(size: Int): List<Int> {
        return (1..size).map { Random.nextInt(1, 10000) }
    }
    
    private fun measureTimeMillis(block: () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
}

// ================================
// Demonstration Functions
// ================================

fun demonstrateDataStructures() {
    println("=== Data Structures Demo ===")
    
    // Dynamic Array
    println("\n--- Dynamic Array ---")
    val dynamicArray = DynamicArray<Int>()
    (1..5).forEach { dynamicArray.add(it) }
    println("Array: $dynamicArray")
    dynamicArray.add(2, 99)
    println("After inserting 99 at index 2: $dynamicArray")
    dynamicArray.removeAt(2)
    println("After removing index 2: $dynamicArray")
    
    // Linked List
    println("\n--- Linked List ---")
    val linkedList = LinkedList<String>()
    linkedList.addLast("Apple")
    linkedList.addLast("Banana")
    linkedList.addFirst("Orange")
    println("Linked List: $linkedList")
    println("Contains 'Banana': ${linkedList.contains("Banana")}")
    
    // Stack
    println("\n--- Stack ---")
    val stack = Stack<Int>()
    (1..5).forEach { stack.push(it) }
    println("Stack after pushing 1-5: $stack")
    println("Popped: ${stack.pop()}")
    println("Stack after pop: $stack")
    
    // Queue
    println("\n--- Queue ---")
    val queue = Queue<String>()
    listOf("First", "Second", "Third").forEach { queue.enqueue(it) }
    println("Queue: $queue")
    println("Dequeued: ${queue.dequeue()}")
    println("Queue after dequeue: $queue")
    
    // Binary Search Tree
    println("\n--- Binary Search Tree ---")
    val bst = BinarySearchTree<Int>()
    listOf(5, 3, 7, 2, 4, 6, 8).forEach { bst.insert(it) }
    println("In-order traversal: ${bst.inOrderTraversal()}")
    println("Contains 4: ${bst.contains(4)}")
    println("Contains 9: ${bst.contains(9)}")
    println("Tree height: ${bst.height()}")
    
    // Hash Table
    println("\n--- Hash Table ---")
    val hashTable = HashTable<String, Int>()
    hashTable.put("Alice", 25)
    hashTable.put("Bob", 30)
    hashTable.put("Charlie", 35)
    println("Alice's age: ${hashTable.get("Alice")}")
    println("Keys: ${hashTable.keys()}")
    println("Load factor: ${String.format("%.2f", hashTable.loadFactor())}")
}

fun demonstrateAlgorithms() {
    println("\n=== Algorithms Demo ===")
    
    // Sorting
    println("\n--- Sorting ---")
    val unsortedList = mutableListOf(64, 34, 25, 12, 22, 11, 90)
    println("Original: $unsortedList")
    
    val quickSorted = unsortedList.toMutableList()
    SortingAlgorithms.quickSort(quickSorted)
    println("Quick Sort: $quickSorted")
    
    val mergeSorted = unsortedList.toMutableList()
    SortingAlgorithms.mergeSort(mergeSorted)
    println("Merge Sort: $mergeSorted")
    
    // Searching
    println("\n--- Searching ---")
    val sortedList = listOf(2, 5, 8, 12, 16, 23, 38, 45, 56, 67, 78)
    val target = 23
    println("Sorted list: $sortedList")
    println("Binary search for $target: ${SearchAlgorithms.binarySearch(sortedList, target)}")
    println("Linear search for $target: ${SearchAlgorithms.linearSearch(sortedList, target)}")
    
    // Dynamic Programming
    println("\n--- Dynamic Programming ---")
    println("Fibonacci(10): ${DynamicProgramming.fibonacci(10)}")
    println("LCS of 'ABCDGH' and 'AEDFHR': ${DynamicProgramming.longestCommonSubsequence("ABCDGH", "AEDFHR")}")
    
    val weights = intArrayOf(10, 20, 30)
    val values = intArrayOf(60, 100, 120)
    val capacity = 50
    println("Knapsack (weights=$weights, values=$values, capacity=$capacity): ${DynamicProgramming.knapsack(weights, values, capacity)}")
    
    // Problem Solving
    println("\n--- Problem Solving ---")
    val numbers = intArrayOf(2, 7, 11, 15)
    val targetSum = 9
    println("Two sum ($numbers, target=$targetSum): ${ProblemSolving.twoSum(numbers, targetSum)}")
    
    val parentheses = "([{}])"
    println("Valid parentheses '$parentheses': ${ProblemSolving.isValidParentheses(parentheses)}")
    
    val subarray = intArrayOf(-2, 1, -3, 4, -1, 2, 1, -5, 4)
    println("Max subarray sum ($subarray): ${ProblemSolving.maxSubarraySum(subarray)}")
}

fun main() {
    println("🧮 Data Structures and Algorithms in Kotlin")
    println("=" * 50)
    
    demonstrateDataStructures()
    demonstrateAlgorithms()
    
    // Run benchmarks for smaller datasets
    AlgorithmBenchmark.benchmarkSortingAlgorithms(listOf(1000, 5000))
    AlgorithmBenchmark.benchmarkSearchAlgorithms()
    
    println("\n=== Summary ===")
    println("✓ Custom data structure implementations")
    println("✓ Classical sorting algorithms with complexity analysis")
    println("✓ Search algorithms for different scenarios")
    println("✓ Dynamic programming solutions")
    println("✓ Common problem-solving patterns")
    println("✓ Performance benchmarking tools")
    
    println("\n💡 Key Takeaways:")
    println("• Choose the right data structure for your use case")
    println("• Understand time and space complexity trade-offs")
    println("• Practice problem decomposition and pattern recognition")
    println("• Measure performance with realistic datasets")
    println("• Consider both average and worst-case scenarios")
    println("• Master fundamental algorithms before optimizing")
}

/**
 * TODO: Advanced Algorithm Topics
 * 
 * 1. Advanced graph algorithms (Dijkstra, A*, MST)
 * 2. String matching algorithms (KMP, Rabin-Karp)
 * 3. Advanced tree structures (AVL, Red-Black, B-trees)
 * 4. Geometric algorithms
 * 5. Network flow algorithms
 * 6. Approximation algorithms
 * 7. Parallel and concurrent algorithms
 * 8. Cache-efficient algorithms
 * 9. Randomized algorithms
 * 10. Advanced dynamic programming patterns
 * 11. Segment trees and Fenwick trees
 * 12. Trie and suffix tree implementations
 * 13. Union-Find data structure
 * 14. Skip lists and probabilistic data structures
 * 15. Algorithm visualization and analysis tools
 */