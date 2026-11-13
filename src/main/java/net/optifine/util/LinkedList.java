package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T> {
   private int size;
   private LinkedList.Node<T> last;
   private LinkedList.Node<T> first;

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();

      for (Iterator<LinkedList.Node<T>> iterator = this.iterator(); iterator.hasNext();) {
         LinkedList.Node<T> node = iterator.next();
         if (stringBuffer.length() > 0) {
            stringBuffer.append(", ");
         }

         stringBuffer.append(node.getItem());
      }

      return this.size + " [" + stringBuffer.toString() + "]";
   }

   private void checkNoParent(LinkedList.Node<T> node) {
      if (node.parent != null) {
         throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
      }
   }

   public LinkedList.Node<T> getFirst() {
      return this.first;
   }

   public boolean contains(LinkedList.Node<T> node) {
      return node.parent == this;
   }

   public int getSize() {
      return this.size;
   }

   public LinkedList.Node<T> remove(LinkedList.Node<T> tNode) {
      this.checkParent(tNode);
      LinkedList.Node<T> prev = tNode.getPrev();
      LinkedList.Node<T> next = tNode.getNext();
      if (prev != null) {
         prev.setNext(next);
      } else {
         this.first = next;
      }

      if (next != null) {
         next.setPrev(prev);
      } else {
         this.last = prev;
      }

      tNode.setPrev((LinkedList.Node<T>)null);
      tNode.setNext((LinkedList.Node<T>)null);
      tNode.setParent((LinkedList<T>)null);
      this.size--;
      return tNode;
   }

   public Iterator<LinkedList.Node<T>> iterator() {
      return new Iterator<LinkedList.Node<T>>() {
         LinkedList.Node<T> node = LinkedList.this.getFirst();

         public LinkedList.Node<T> next() {
            LinkedList.Node<T> node = this.node;
            if (this.node != null) {
               this.node = this.node.next;
            }

            return node;
         }

         @Override
         public boolean hasNext() {
            return this.node != null;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("remove");
         }
      };
   }

   public LinkedList.Node<T> getLast() {
      return this.last;
   }

   public boolean isEmpty() {
      return this.size <= 0;
   }

   private void checkParent(LinkedList.Node<T> node) {
      if (node.parent != this) {
         throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
      }
   }

   public void addLast(LinkedList.Node<T> tNode) {
      this.checkNoParent(tNode);
      if (this.isEmpty()) {
         this.first = tNode;
         this.last = tNode;
      } else {
         LinkedList.Node<T> node = this.last;
         tNode.setPrev(node);
         node.setNext(tNode);
         this.last = tNode;
      }

      tNode.setParent(this);
      this.size++;
   }

   public static class Node<T> {
      private LinkedList<T> parent;
      private LinkedList.Node<T> prev;
      private final T item;
      private LinkedList.Node<T> next;

      private void setNext(LinkedList.Node<T> next) {
         this.next = next;
      }

      public LinkedList.Node<T> getPrev() {
         return this.prev;
      }

      public T getItem() {
         return this.item;
      }

      public LinkedList.Node<T> getNext() {
         return this.next;
      }

      private void setPrev(LinkedList.Node<T> prev) {
         this.prev = prev;
      }

      public Node(T item) {
         this.item = item;
      }

      private void setParent(LinkedList<T> parent) {
         this.parent = parent;
      }

      @Override
      public String toString() {
         return this.item + "";
      }
   }
}
