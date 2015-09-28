/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package android.widget.espresso;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.util.HumanReadables;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;

/**
 * A collection of actions on a {@link android.widget.TextView}.
 */
public final class TextViewActions {

    private TextViewActions() {}

    /**
     * Returns an action that clicks on text at an index on the TextView.<br>
     * <br>
     * View constraints:
     * <ul>
     * <li>must be a TextView displayed on screen
     * <ul>
     *
     * @param index The index of the TextView's text to click on.
     */
    public static ViewAction clickOnTextAtIndex(int index) {
        return actionWithAssertions(
                new GeneralClickAction(Tap.SINGLE, new TextCoordinates(index), Press.FINGER));
    }

    /**
     * Returns an action that double-clicks on text at an index on the TextView.<br>
     * <br>
     * View constraints:
     * <ul>
     * <li>must be a TextView displayed on screen
     * <ul>
     *
     * @param index The index of the TextView's text to double-click on.
     */
    public static ViewAction doubleClickOnTextAtIndex(int index) {
        return actionWithAssertions(
                new GeneralClickAction(Tap.DOUBLE, new TextCoordinates(index), Press.FINGER));
    }

    /**
     * Returns an action that long presses on text at an index on the TextView.<br>
     * <br>
     * View constraints:
     * <ul>
     * <li>must be a TextView displayed on screen
     * <ul>
     *
     * @param index The index of the TextView's text to long press on.
     */
    public static ViewAction longPressOnTextAtIndex(int index) {
        return actionWithAssertions(
                new GeneralClickAction(Tap.LONG, new TextCoordinates(index), Press.FINGER));
    }

    /**
     * Returns an action that long presses then drags on text from startIndex to endIndex on the
     * TextView.<br>
     * <br>
     * View constraints:
     * <ul>
     * <li>must be a TextView displayed on screen
     * <ul>
     *
     * @param startIndex The index of the TextView's text to start a drag from
     * @param endIndex The index of the TextView's text to end the drag at
     */
    public static ViewAction longPressAndDragOnText(int startIndex, int endIndex) {
        return actionWithAssertions(
                new DragOnTextViewActions(
                        DragOnTextViewActions.Drag.LONG_PRESS,
                        new TextCoordinates(startIndex),
                        new TextCoordinates(endIndex),
                        Press.FINGER));
    }

    /**
     * Returns an action that double taps then drags on text from startIndex to endIndex on the
     * TextView.<br>
     * <br>
     * View constraints:
     * <ul>
     * <li>must be a TextView displayed on screen
     * <ul>
     *
     * @param startIndex The index of the TextView's text to start a drag from
     * @param endIndex The index of the TextView's text to end the drag at
     */
    public static ViewAction doubleTapAndDragOnText(int startIndex, int endIndex) {
        return actionWithAssertions(
                new DragOnTextViewActions(
                        DragOnTextViewActions.Drag.DOUBLE_TAP,
                        new TextCoordinates(startIndex),
                        new TextCoordinates(endIndex),
                        Press.FINGER));
    }

    /**
     * A provider of the x, y coordinates of the text at the specified index in a text view.
     */
    private static final class TextCoordinates implements CoordinatesProvider {

        private final int mIndex;
        private final String mActionDescription;

        public TextCoordinates(int index) {
            mIndex = index;
            mActionDescription = "Could not locate text at index: " + mIndex;
        }

        @Override
        public float[] calculateCoordinates(View view) {
            try {
                return locateTextAtIndex((TextView) view, mIndex);
            } catch (ClassCastException e) {
                throw new PerformException.Builder()
                        .withActionDescription(mActionDescription)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(e)
                        .build();
            } catch (StringIndexOutOfBoundsException e) {
                throw new PerformException.Builder()
                        .withActionDescription(mActionDescription)
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(e)
                        .build();
            }
        }

        /**
         * @throws StringIndexOutOfBoundsException
         */
        private float[] locateTextAtIndex(TextView textView, int index) {
            if (index < 0 || index > textView.getText().length()) {
                throw new StringIndexOutOfBoundsException(index);
            }
            final int[] xy = new int[2];
            textView.getLocationOnScreen(xy);
            final Layout layout = textView.getLayout();
            final int line = layout.getLineForOffset(index);
            final float x = textView.getTotalPaddingLeft() - textView.getScrollX()
                    + layout.getPrimaryHorizontal(index);
            final float y = textView.getTotalPaddingTop() - textView.getScrollY()
                    + layout.getLineTop(line);
            return new float[]{x + xy[0], y + xy[1]};
        }
    }
}
