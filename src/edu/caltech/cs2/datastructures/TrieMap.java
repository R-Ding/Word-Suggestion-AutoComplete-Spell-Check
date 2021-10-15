package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class TrieMap<A, K extends Iterable<A>, V> implements ITrieMap<A, K, V> {
    private TrieNode<A, V> root;
    private Function<IDeque<A>, K> collector;
    private int size;

    public TrieMap(Function<IDeque<A>, K> collector) {
        this.root = new TrieNode<>();
        this.collector = collector;
        this.size = 0;
    }

    @Override
    public boolean isPrefix(K key) {
        TrieNode<A, V> current = this.root;
        if (current.pointers.keySet().isEmpty()) {
            return false;
        }
        for (A element : key) {
            if (!current.pointers.keySet().contains(element)) {
                return false;
            }
            current = current.pointers.get(element);
        }
        return true;
    }

    private void getCompletions(TrieNode<A, V> current, IDeque<V> result) {
        if (current.value != null) {
            result.addBack(current.value);
        }
        for (A a : current.pointers.keySet()) {
            getCompletions(current.pointers.get(a), result);
        }
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        IDeque<V> completions = new LinkedDeque<V>();
        TrieNode<A, V> current = getNode(prefix);
        if(current == null){
            return completions;
        }
        getCompletions(current, completions);
        return completions;
    }

    private TrieNode<A, V> getNode(K prefix){
        TrieNode<A, V> current = this.root;
        if (current.pointers.keySet().isEmpty()) {
            return null;
        }
        for (A element : prefix) {
            if (current.pointers.keySet().contains(element)) {
                current = current.pointers.get(element);
            }
            else{
                return null;
            }
        }
        return current;
    }


    @Override
    public void clear() {

    }

    @Override
    public V get(K key) {
        TrieNode<A, V> x = getNode(key);
        if(x == null){
            return null;
        }
        else{
            return x.value;
        }
    }


    @Override
    public V remove(K key) {
        V output = get(key);
        remove(this.root, key.iterator());
        return output;
    }

    private boolean remove(TrieNode<A, V> current, Iterator<A> letter) {
        if(current == null){
            return false;
        }
        if (!letter.hasNext()) {
            if(current.value != null){
                this.size--;
                current.value = null;
            }
            if (current.pointers.isEmpty()) {
                return true;
            }
            return false;
        }
        else {
            A letter2 = letter.next();
            boolean value = remove(current.pointers.get(letter2), letter);
            if (value) {
                current.pointers.remove(letter2);
            }
            if (current.value != null || !current.pointers.isEmpty()) {
                return false;
            }
            return true;
        }
    }


    @Override
    public V put(K key, V value) {
        TrieNode<A, V> node = this.root;

        for (A a : key) {
            if (node.pointers.keySet().contains(a)) {
                node = node.pointers.get(a);
            }
            else {
                TrieNode<A, V> x = new TrieNode<>();
                node.pointers.put(a, x);
                node = x;
            }
        }
        if(node.value == null){
            size++;
        }
        V val = node.value;
        node.value = value;
        return val;
    }

    @Override
    public boolean containsKey(K key) {
        return (get(key) != null);
    }

    private boolean containsValue(TrieNode<A, V> current, V value) {
        if (current.value == value) {
            return true;
        }
        else {
            for (TrieNode<A, V> d : current.pointers.values()) {
                if (containsValue(d, value)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean containsValue(V value) {
        return containsValue(this.root, value);
    }

    @Override
    public int size() {
        return this.size;
    }


    @Override
    public ICollection<K> keySet() {
        IDeque<K> output = new LinkedDeque<K>();
        IDeque<A> init = new LinkedDeque<A>();
        keySet(this.root, output, init);

        return output;
    }

    private void keySet(TrieNode<A, V> current, IDeque<K> result, IDeque<A> acc){
        if (current.value != null) {
            result.addBack(collector.apply(acc));
        }
        for (A a : current.pointers.keySet()) {
            acc.addBack(a);
            keySet(current.pointers.get(a), result, acc);
            acc.removeBack();
        }
    }


    private void values(TrieNode<A, V> current, ICollection<V> result) {
        if (current.value != null && !result.contains(current.value)) {
            result.add(current.value);

        }
        for (A a : current.pointers.keySet()) {
            values(current.pointers.get(a), result);
        }
    }

    public ICollection<V> values() {
        ICollection<V> vals = new LinkedDeque<V>();
        values(this.root, vals);
        return vals;
    }


    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
    
    private static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }
}
