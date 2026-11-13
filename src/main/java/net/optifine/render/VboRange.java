package net.optifine.render;


public class VboRange {
   private int position = -1;
   private int size = 0;
   private net.optifine.util.LinkedList.Node<VboRange> node = new net.optifine.util.LinkedList.Node<>(this);

   public void setSize(int size) {
      this.size = size;
   }

   public int getSize() {
      return this.size;
   }

   @Override
   public String toString() {
      return this.position + "/" + this.size + "/" + (this.position + this.size);
   }

   public net.optifine.util.LinkedList.Node<VboRange> getNode() {
      return this.node;
   }

   public int getPositionNext() {
      return this.position + this.size;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public int getPosition() {
      return this.position;
   }

   public VboRange getNext() {
      net.optifine.util.LinkedList.Node<VboRange> node = this.node.getNext();
      return node == null ? null : node.getItem();
   }

   public VboRange getPrev() {
      net.optifine.util.LinkedList.Node<VboRange> node = this.node.getPrev();
      return node == null ? null : node.getItem();
   }
}
