package edu.caltech.cs2.interfaces;

public interface IList<E> extends ICollection<E> {
  public E get(int index);
  public E set(int index, E element);
  public E remove(int index);
}
