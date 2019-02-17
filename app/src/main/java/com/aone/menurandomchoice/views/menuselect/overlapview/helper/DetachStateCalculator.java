package com.aone.menurandomchoice.views.menuselect.overlapview.helper;

import android.graphics.RectF;

import com.aone.menurandomchoice.utils.DisplayUtil;

import androidx.annotation.NonNull;

public class DetachStateCalculator {

    private static final float DETACH_ALLOW_RANGE = 0.35f;
    private static final float ALLOWABLE_POSITIVE_SPEED = 50;
    private static final float ALLOWABLE_NEGATIVE_SPEED = -50;

    private RectF oldViewRect;
    private RectF newViewRect;
    private float xSpeedWithBasedDP;
    private float ySpeedWithBasedDP;

    enum Direction {
        NONE, LEFT, RIGHT, UP, DOWN, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN
    }

    public boolean isPossibleDetachView(@NonNull RectF oldViewRect, @NonNull RectF newViewRect, float xSpeedWithBasedPX, float ySpeedWithBasedPX) {
        recordViewRect(oldViewRect, newViewRect);
        recordDragSpeedAfterConvertDP(xSpeedWithBasedPX, ySpeedWithBasedPX);

        Direction dragDirection = calculateDragDirection();
        Direction detachDirection = calculateDetachDirection();

        RectF rectOfAllowableLine = calculateRectOfAllowableLine();
        boolean isPassedAllowableLine = isPassedAllowableLine(rectOfAllowableLine);

        return calculateDetachPossibility(isPassedAllowableLine, dragDirection, detachDirection);
    }

    private void recordViewRect(RectF oldViewRect, RectF newViewRect) {
        this.oldViewRect = oldViewRect;
        this.newViewRect = newViewRect;
    }

    private void recordDragSpeedAfterConvertDP(float xSpeedWithBasedPX, float ySpeedWithBasedPX) {
        xSpeedWithBasedDP = DisplayUtil.convertToDP(xSpeedWithBasedPX);
        ySpeedWithBasedDP = DisplayUtil.convertToDP(ySpeedWithBasedPX);
    }

    private RectF calculateRectOfAllowableLine() {
        float viewWidth = oldViewRect.right - oldViewRect.left;
        float viewHeight = oldViewRect.bottom - oldViewRect.top;

        float allowableWidth = viewWidth * DETACH_ALLOW_RANGE;
        float allowableHeight = viewHeight * DETACH_ALLOW_RANGE;

        float detachLineOfLeft = newViewRect.left + allowableWidth;
        float detachLineOfTop = newViewRect.top + allowableHeight;
        float detachLineOfRight = newViewRect.right - allowableWidth;
        float detachLineOfBottom = newViewRect.bottom - allowableHeight;

        return new RectF(detachLineOfLeft, detachLineOfTop, detachLineOfRight, detachLineOfBottom);
    }

    private boolean isPassedAllowableLine(RectF rectOfAllowableLine) {
        return (rectOfAllowableLine.left <= oldViewRect.left)
                || (oldViewRect.right <= rectOfAllowableLine.right)
                || (rectOfAllowableLine.top <= oldViewRect.top)
                || (oldViewRect.bottom <= rectOfAllowableLine.bottom);
    }

    private Direction calculateDragDirection() {
        if(isSlowDragSpeed()) {
            return Direction.NONE;
        } else {
            if (isRightDrag()) {
                if (isDownDrag()) {
                    return Direction.RIGHT_DOWN;
                } else if (isUpDrag()) {
                    return Direction.RIGHT_UP;
                } else {
                    return Direction.RIGHT;
                }
            } else if (isLeftDrag()) {
                if (isDownDrag()) {
                    return Direction.LEFT_DOWN;
                } else if (isUpDrag()) {
                    return Direction.LEFT_UP;
                } else {
                    return Direction.LEFT;
                }
            } else {
                if (isDownDrag()) {
                    return Direction.DOWN;
                } else if (isUpDrag()) {
                    return Direction.UP;
                } else {
                    return Direction.NONE;
                }
            }
        }
    }

    private boolean isSlowDragSpeed() {
        return ALLOWABLE_NEGATIVE_SPEED <= xSpeedWithBasedDP
                && xSpeedWithBasedDP <= ALLOWABLE_POSITIVE_SPEED
                && ALLOWABLE_NEGATIVE_SPEED <= ySpeedWithBasedDP
                && ySpeedWithBasedDP <= ALLOWABLE_POSITIVE_SPEED;
    }

    private Direction calculateDetachDirection() {
        if(isReachedRight()) {
            if(isReachedBottom()) {
                return Direction.RIGHT_DOWN;
            } else if(isReachedTop()) {
                return Direction.RIGHT_UP;
            } else {
                return Direction.RIGHT;
            }
        } else if(isReachedLeft()) {
            if(isReachedBottom()) {
                return Direction.LEFT_DOWN;
            } else if(isReachedTop()) {
                return Direction.LEFT_UP;
            } else {
                return Direction.LEFT;
            }
        } else {
            if(isReachedBottom()) {
                return Direction.DOWN;
            } else if(isReachedTop()) {
                return Direction.UP;
            } else {
                return Direction.NONE;
            }
        }
    }

    private boolean calculateDetachPossibility(boolean isPassedAllowableLine, Direction dragDirection, Direction detachDirection) {
        if(isPassedAllowableLine) {
            switch (detachDirection) {
                case LEFT:
                    return !isRightDirectionOfDrag(dragDirection);
                case RIGHT:
                    return !isLeftDirectionOfDrag(dragDirection);
                case UP:
                    return !isDownDirectionOfDrag(dragDirection);
                case DOWN:
                    return !isUpDirectionOfDrag(dragDirection);
                case LEFT_UP:
                    return !isRightDirectionOfDrag(dragDirection);
                case LEFT_DOWN:
                    return !isRightDirectionOfDrag(dragDirection);
                case RIGHT_UP:
                    return !isLeftDirectionOfDrag(dragDirection);
                case RIGHT_DOWN:
                    return !isLeftDirectionOfDrag(dragDirection);
                    default:
                        return true;
            }
        } else {
            if(!isDirectionOfDrag(dragDirection)) {
                return false;
            } else {
                switch (detachDirection) {
                    case LEFT:
                        return !isRightDirectionOfDrag(dragDirection);
                    case RIGHT:
                        return !isLeftDirectionOfDrag(dragDirection);
                    case UP:
                        return !isDownDirectionOfDrag(dragDirection);
                    case DOWN:
                        return !isUpDirectionOfDrag(dragDirection);
                    case LEFT_UP:
                        return dragDirection != Direction.RIGHT_DOWN;
                    case LEFT_DOWN:
                        return dragDirection != Direction.RIGHT_UP;
                    case RIGHT_UP:
                        return dragDirection != Direction.LEFT_DOWN;
                    case RIGHT_DOWN:
                        return dragDirection != Direction.LEFT_UP;
                    default:
                        return false;
                }
            }
        }
    }

    private boolean isRightDrag() {
        return 0 < xSpeedWithBasedDP;
    }

    private boolean isLeftDrag() {
        return xSpeedWithBasedDP < 0;
    }

    private boolean isUpDrag() {
        return ySpeedWithBasedDP < 0;
    }

    private boolean isDownDrag() {
        return 0 < ySpeedWithBasedDP;
    }

    private boolean isReachedLeft() {
        return newViewRect.left <= oldViewRect.left;
    }

    private boolean isReachedRight() {
        return oldViewRect.right <= newViewRect.right;
    }

    private boolean isReachedTop() {
        return newViewRect.top <= oldViewRect.top;
    }

    private boolean isReachedBottom() {
        return oldViewRect.bottom <= newViewRect.bottom;
    }

    private boolean isRightDirectionOfDrag(Direction dragDirection) {
        return dragDirection == Direction.RIGHT
                || dragDirection == Direction.RIGHT_UP
                || dragDirection == Direction.RIGHT_DOWN;
    }

    private boolean isLeftDirectionOfDrag(Direction dragDirection) {
        return dragDirection == Direction.LEFT
                || dragDirection == Direction.LEFT_UP
                || dragDirection == Direction.LEFT_DOWN;
    }

    private boolean isUpDirectionOfDrag(Direction dragDirection) {
        return dragDirection == Direction.UP
                || dragDirection == Direction.LEFT_UP
                || dragDirection == Direction.RIGHT_UP;
    }

    private boolean isDownDirectionOfDrag(Direction dragDirection) {
        return dragDirection == Direction.DOWN
                || dragDirection == Direction.LEFT_DOWN
                || dragDirection == Direction.RIGHT_DOWN;
    }

    private boolean isDirectionOfDrag(Direction dragDirection) {
        return dragDirection != Direction.NONE;
    }
}
