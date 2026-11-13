package wtf.novoline.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.RoundedPolygonRenderer;

public class TextSelection {
   protected TextSelection.Contract contract = TextSelection.Contract.identity();
   protected final TextSelection.TextBuffer textBuffer = new TextSelection.TextBuffer();
   protected boolean focused;
   protected final TextSelection.Cursor cursor = new TextSelection.Cursor();
   protected final TextSelection.Selection selection = new TextSelection.Selection();
   protected final CFontRenderer cFontRenderer;
   protected final BlurRenderer blurRenderer;
   protected final int x;
   protected final int y;
   protected final int width;
   protected final int height;
   protected boolean visible;

   private static IllegalArgumentException emptyTextException() {
      return new IllegalArgumentException("Text is empty");
   }

   public void updateFocus(int i, int i2) {
      this.focused = i >= this.x && i <= this.x + this.width && i2 >= this.y && i2 <= this.y + this.height;
   }

   public int getHeight() {
      return this.height;
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   public TextSelection.TextBuffer getTextBuffer() {
      return this.textBuffer;
   }

   public int getX() {
      return this.x;
   }

   public TextSelection.Selection getSelection() {
      return this.selection;
   }

   private static int resolvePosition(TextSelection.StartType startType, TextSelection.TextBuffer textBuffer2, int i) {
      return switch (startType) {
         case START -> 0;
         case END -> textBuffer2.length();
         case ONE_BACKWARD -> i < 1 ? i : i - 1;
         case ONE_FORWARD -> i >= textBuffer2.length() ? i : i + 1;
      };
   }

   public boolean isFocused() {
      return this.focused;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public TextSelection withMask(String text, Predicate predicate) {
      return this.withContract(TextSelection.Contract.createMask(text, predicate));
   }

   public int getWidth() {
      return this.width;
   }

   public TextSelection(CFontRenderer cFontRenderer2, BlurRenderer blurRenderer2, int i, int i2) {
      this.cFontRenderer = cFontRenderer2;
      this.blurRenderer = blurRenderer2;
      this.x = i;
      this.y = i2;
      this.width = 200;
      this.height = 20;
   }

   public int getY() {
      return this.y;
   }

   private static IllegalStateException inactiveSelectionException() {
      return new IllegalStateException("Selection is inactive");
   }

   public TextSelection.Cursor getCursor() {
      return this.cursor;
   }

   public boolean keyTyped(char c, int i) {
      boolean ctrlKeyDown = GuiScreen.isCtrlKeyDown();
      boolean altKeyDown = GuiScreen.isAltKeyDown();
      boolean shiftKeyDown = GuiScreen.isShiftKeyDown();
      if (this.focused && !altKeyDown) {
         if (ctrlKeyDown) {
            switch (i) {
               case 14:
                  if (this.selection.hasSelection()) {
                     this.selection.deleteSelection();
                  } else {
                     this.cursor.deleteTo(TextSelection.StartType.START);
                  }
                  break;
               case 30:
                  this.selection.moveAnchor(TextSelection.StartType.START);
                  this.selection.moveCaret(TextSelection.StartType.END);
                  break;
               case 45:
                  if (this.selection.hasSelection()) {
                     String text = this.selection.cutText();
                     TextSelection.Clipboard.copyToClipboard(text);
                  }
                  break;
               case 46:
                  if (this.selection.hasSelection()) {
                     String selectedText = this.selection.getSelectedText();
                     TextSelection.Clipboard.copyToClipboard(selectedText);
                  }
                  break;
               case 47:
                  Optional clipboard = TextSelection.Clipboard.readClipboard();
                  if (!clipboard.isEmpty()) {
                     String text2 = (String)clipboard.get();
                     if (this.selection.hasSelection()) {
                        this.selection.replaceWithText(text2);
                     } else {
                        this.textBuffer.insertText(text2);
                     }
                  }
                  break;
               case 199:
               case 203:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.START);
                  } else {
                     this.cursor.move(TextSelection.StartType.START);
                  }
                  break;
               case 205:
               case 207:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.END);
                  } else {
                     this.cursor.move(TextSelection.StartType.END);
                  }
                  break;
               case 211:
                  if (this.selection.hasSelection()) {
                     this.selection.deleteSelection();
                  } else {
                     this.cursor.deleteTo(TextSelection.StartType.END);
                  }
            }
         } else {
            switch (i) {
               case 14:
                  if (this.selection.hasSelection()) {
                     this.selection.deleteSelection();
                  } else {
                     this.cursor.deleteTo(TextSelection.StartType.ONE_BACKWARD);
                  }
                  break;
               case 199:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.START);
                  } else {
                     this.cursor.move(TextSelection.StartType.START);
                  }
                  break;
               case 203:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.ONE_BACKWARD);
                  } else if (this.selection.hasSelection()) {
                     this.cursor.moveTo(this.selection.getStart());
                  } else {
                     this.cursor.move(TextSelection.StartType.ONE_BACKWARD);
                  }
                  break;
               case 205:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.ONE_FORWARD);
                  } else if (this.selection.hasSelection()) {
                     this.cursor.moveTo(this.selection.getEnd());
                  } else {
                     this.cursor.move(TextSelection.StartType.ONE_FORWARD);
                  }
                  break;
               case 207:
                  if (shiftKeyDown) {
                     this.selection.moveCaret(TextSelection.StartType.END);
                  } else {
                     this.cursor.move(TextSelection.StartType.END);
                  }
                  break;
               case 211:
                  if (this.selection.hasSelection()) {
                     this.selection.deleteSelection();
                  } else {
                     this.cursor.deleteTo(TextSelection.StartType.ONE_FORWARD);
                  }
                  break;
               default:
                  if (this.selection.hasSelection()) {
                     this.selection.replaceWithChar(c);
                  } else {
                     this.textBuffer.typeChar(c);
                  }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void draw(float f) {
      GlStateManager.pushMatrix();
      if (!this.blurRenderer.hasNoResult()) {
         ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
         RoundedPolygonRenderer roundedPolygonRenderer = new RoundedPolygonRenderer(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
         this.blurRenderer.bindResultTexture();
         roundedPolygonRenderer.putRoundedRect(this.x, this.y, this.width, this.height, 5.0F, -1);
         roundedPolygonRenderer.draw();
      }

      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.x, this.y, this.width, this.height, 5.0F, 1342177280);
      filledPolygonRenderer.draw();
      RenderUtils.enableScissor(this.x, this.y, this.width, this.height);
      String text = this.contract.transform(this, this.textBuffer.stringBuilder.toString());
      int i = 0;
      int stringWidth = this.cFontRenderer.getStringWidth(text);
      if (stringWidth >= this.width - 10) {
         i = stringWidth - (this.width - 10);
      }

      String text2 = text.substring(this.cursor.position);
      i -= this.cFontRenderer.getStringWidth(text2);
      if (i > 0) {
         GlStateManager.translate(-i, 0.0F, 0.0F);
      }

      int stringWidth2 = this.cFontRenderer.getStringWidth(text.substring(0, this.cursor.position));
      int i2 = this.y + this.height / 2;
      if (this.focused) {
         RenderUtils.drawLine(
            this.x + stringWidth2 + 5.5F,
            i2 - 5,
            this.x + stringWidth2 + 5.5F,
            i2 + 5,
            RenderUtils.withAlphaFraction(-1, (float)InterpolationUtils.oscillate(0.0, 1.0, 500L))
         );
      }

      if (this.selection.hasSelection()) {
         String text3 = text.substring(0, this.selection.anchor);
         int stringWidth3 = this.cFontRenderer.getStringWidth(text3);
         int i3;
         int i4;
         if (this.selection.isAnchorBeforeCaret()) {
            i4 = stringWidth3;
            i3 = stringWidth2 - stringWidth3;
         } else {
            i4 = stringWidth2;
            i3 = stringWidth3 - stringWidth2;
         }

         RenderUtils.drawRect(this.x + 5 + i4, i2 - 5, this.x + 5 + i4 + i3, i2 + 5, 822083583);
      }

      this.cFontRenderer.drawString(text, this.x + 5, this.y + this.height / 2.0F - this.cFontRenderer.getHeight() / 4.0F, -1);
      RenderUtils.disableScissor();
      GlStateManager.popMatrix();
   }

   private static IndexOutOfBoundsException negativePositionException(int i) {
      return new IndexOutOfBoundsException("Negative position: " + i);
   }

   public void focus() {
      this.focused = true;
   }

   private static IndexOutOfBoundsException outOfBoundsException(int i, int i2) {
      return new IndexOutOfBoundsException("Out of bounds: %d/%d".formatted(i, i2));
   }

   private static IndexOutOfBoundsException rangeOutOfBoundsException(int i, int i2, int i3) {
      return new IndexOutOfBoundsException("Range out of bounds: [%d..%d] with length of %d".formatted(i, i2, i3));
   }

   public TextSelection withContract(TextSelection.Contract contract2) {
      if (this.contract == TextSelection.Contract.identity()) {
         this.contract = contract2;
      } else {
         this.contract = this.contract.andThen(contract2);
      }

      return this;
   }

   private IndexOutOfBoundsException badRangeException(int i, int i2) {
      return new IndexOutOfBoundsException("Bad range: %d/%d".formatted(i, i2));
   }

   private static IllegalStateException activeSelectionException() {
      return new IllegalStateException("Selection is active");
   }

   static final class Clipboard {
      public static void copyToClipboard(String text) {
         StringSelection stringSelection = new StringSelection(text);
         getClipboard().setContents(stringSelection, stringSelection);
      }

      public static Optional readClipboard() {
         Transferable contents = getClipboard().getContents(null);
         if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
               return Optional.of(contents.getTransferData(DataFlavor.stringFlavor).toString());
            } catch (Exception exception) {
            }
         }

         return Optional.empty();
      }

      private static java.awt.datatransfer.Clipboard getClipboard() {
         return Toolkit.getDefaultToolkit().getSystemClipboard();
      }

      private Clipboard() {
      }
   }

   @FunctionalInterface
   public interface Contract {
      static TextSelection.Contract identity() {
         return (textSelection, string) -> string;
      }

      static TextSelection.Contract createMask(final String text, final Predicate<TextSelection> predicate) {
         return new TextSelection.Contract() {
            private static final MutableMap<String, MutableIntObjectMap<String>> mutableMap = Maps.mutable.ofInitialCapacity(1);
            private final MutableIntObjectMap<String> mutableIntObjectMap = findCache(text);

            @Override
            public String transform(TextSelection textSelection2, String text2) {
               if (!predicate.test(textSelection2)) {
                  return text2;
               }

               int length2 = text2.length();
               return this.mutableIntObjectMap.getIfAbsentPut(length2, text.repeat(length2));
            }

            private static MutableIntObjectMap<String> findCache(String text3) {
               return mutableMap.getIfAbsentPut(text3, () -> IntObjectMaps.mutable.ofInitialCapacity(32));
            }
         };
      }

      String transform(TextSelection textSelection2, String text);

      default TextSelection.Contract andThen(TextSelection.Contract contract2) {
         return (textSelection, string) -> {
            String text = this.transform(textSelection, string);
            return contract2.transform(textSelection, text);
         };
      }
   }

   public final class Cursor {
      private int position;

      public void moveTo(int i) {
         this.position = TextSelection.this.textBuffer.checkPosition(i);
         TextSelection.this.selection.collapse();
      }

      public void move(TextSelection.StartType startType) {
         this.position = TextSelection.resolvePosition(startType, TextSelection.this.textBuffer, this.position);
         TextSelection.this.selection.collapse();
      }

      public void deleteTo(TextSelection.StartType startType) {
         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         switch (startType) {
            case START:
               TextSelection.this.textBuffer.deleteRange(0, TextSelection.this.cursor.position);
               break;
            case END:
               TextSelection.this.textBuffer.deleteRange(TextSelection.this.cursor.position, TextSelection.this.textBuffer.length());
               break;
            case ONE_BACKWARD:
               if (TextSelection.this.cursor.position < 1) {
                  return;
               }

               TextSelection.this.textBuffer.deleteCharAt(TextSelection.this.cursor.position - 1);
               break;
            case ONE_FORWARD:
               if (TextSelection.this.cursor.position >= TextSelection.this.textBuffer.length()) {
                  return;
               }

               TextSelection.this.textBuffer.deleteCharAt(TextSelection.this.cursor.position);
               break;
            default:
               throw new IllegalStateException("Null not allowed");
         }
      }
   }

   public final class Selection {
      private int anchor = 0;

      public void deleteSelection() {
         if (this.isEmpty()) {
            throw TextSelection.inactiveSelectionException();
         }

         if (this.isAnchorBeforeCaret()) {
            TextSelection.this.textBuffer.stringBuilder.delete(this.anchor, TextSelection.this.cursor.position);
            TextSelection.this.cursor.moveTo(this.anchor);
         } else {
            TextSelection.this.textBuffer.stringBuilder.delete(TextSelection.this.cursor.position, this.anchor);
            TextSelection.this.cursor.moveTo(TextSelection.this.cursor.position);
         }

         TextSelection.this.textBuffer.trimCapacity();
      }

      public boolean isEmpty() {
         return TextSelection.this.cursor.position == this.anchor;
      }

      public void moveCaret(TextSelection.StartType startType) {
         TextSelection.this.cursor.position = TextSelection.resolvePosition(startType, TextSelection.this.textBuffer, TextSelection.this.cursor.position);
      }

      public void moveAnchor(TextSelection.StartType startType) {
         this.anchor = TextSelection.resolvePosition(startType, TextSelection.this.textBuffer, this.anchor);
      }

      public boolean isAnchorAfterCaret() {
         return this.anchor > TextSelection.this.cursor.position;
      }

      public String cutText() {
         String selectedText = this.getSelectedText();
         this.deleteSelection();
         return selectedText;
      }

      public void collapse() {
         this.anchor = TextSelection.this.cursor.position;
      }

      public int getStart() {
         return this.isAnchorBeforeCaret() ? this.anchor : TextSelection.this.cursor.position;
      }

      public int getEnd() {
         return this.isAnchorAfterCaret() ? this.anchor : TextSelection.this.cursor.position;
      }

      public void replaceWithChar(char c) {
         this.deleteSelection();
         TextSelection.this.textBuffer.typeChar(c);
      }

      public void replaceWithText(CharSequence charSequence) {
         if (charSequence == null) {
            throw TextSelection.emptyTextException();
         }

         this.deleteSelection();
         TextSelection.this.textBuffer.insertText(charSequence);
      }

      public boolean isAnchorBeforeCaret() {
         return this.anchor < TextSelection.this.cursor.position;
      }

      public String getSelectedText() {
         if (this.isEmpty()) {
            throw TextSelection.inactiveSelectionException();
         } else {
            return this.isAnchorBeforeCaret()
               ? TextSelection.this.textBuffer.substring(this.anchor, TextSelection.this.cursor.position)
               : TextSelection.this.textBuffer.substring(TextSelection.this.cursor.position, this.anchor);
         }
      }

      public boolean hasSelection() {
         return TextSelection.this.cursor.position != this.anchor;
      }
   }

   public enum StartType {
      START,
      END,
      ONE_FORWARD,
      ONE_BACKWARD;
   }

   public final class TextBuffer {
      private final StringBuilder stringBuilder = new StringBuilder();

      public void deleteRange(int i, int i2) {
         this.checkRange(i, i2);
         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         this.stringBuilder.delete(i, i2);
         TextSelection.this.cursor.moveTo(i);
         this.trimCapacity();
      }

      public void setText(CharSequence charSequence) {
         if (charSequence == null) {
            throw TextSelection.emptyTextException();
         }

         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         this.stringBuilder.replace(0, this.stringBuilder.length(), charSequence.toString());
         TextSelection.this.cursor.moveTo(Math.min(TextSelection.this.cursor.position, charSequence.length()));
      }

      public void insertText(CharSequence charSequence) {
         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         int i = 0;
         int i2 = 0;

         for (int length2 = charSequence.length(); i2 < length2; i2++) {
            char c = charSequence.charAt(i2);
            if (ChatAllowedCharacters.isAllowedCharacter(c)) {
               TextSelection.this.textBuffer.stringBuilder.insert(TextSelection.this.cursor.position + i, c);
               i++;
            }
         }

         TextSelection.this.cursor.moveTo(TextSelection.this.cursor.position + i);
      }

      private void trimCapacity() {
         if (this.stringBuilder.capacity() - this.stringBuilder.length() > 16) {
            this.stringBuilder.trimToSize();
         }
      }

      public void deleteCharAt(int i) {
         this.checkPosition(i);
         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         this.stringBuilder.deleteCharAt(i);
         TextSelection.this.cursor.moveTo(i);
         this.trimCapacity();
      }

      public int length() {
         return this.stringBuilder.length();
      }

      private void checkRange(int i, int i2) {
         int length2 = TextSelection.this.textBuffer.length();
         if (i < 0) {
            throw TextSelection.negativePositionException(i);
         }

         if (i > i2) {
            throw TextSelection.this.badRangeException(i, i2);
         }

         if (i2 > length2) {
            throw TextSelection.rangeOutOfBoundsException(i, i2, length2);
         }
      }

      public String getText() {
         return this.stringBuilder.toString();
      }

      public void typeChar(char c) {
         if (TextSelection.this.selection.hasSelection()) {
            throw TextSelection.activeSelectionException();
         }

         if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            this.stringBuilder.insert(TextSelection.this.cursor.position, c);
            TextSelection.this.cursor.move(TextSelection.StartType.ONE_FORWARD);
         }
      }

      private int checkPosition(int i) {
         if (i < 0) {
            throw TextSelection.negativePositionException(i);
         } else if (i > TextSelection.this.textBuffer.length()) {
            throw TextSelection.outOfBoundsException(i, TextSelection.this.textBuffer.length());
         } else {
            return i;
         }
      }

      public String substring(int i, int i2) {
         this.checkRange(i, i2);
         return this.stringBuilder.substring(i, i2);
      }
   }
}
