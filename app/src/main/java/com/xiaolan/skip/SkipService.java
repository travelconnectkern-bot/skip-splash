package com.xiaolan.skip;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class SkipService extends AccessibilityService {

    private static final String[] SKIP_WORDS = {
            "跳过", "跳過", "跳过广告", "跳過廣告", "关闭广告", "關閉廣告", "略過"
    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) {
            return;
        }
        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return;
        }
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            return;
        }
        List<AccessibilityNodeInfo> hits = new ArrayList<>();
        collect(root, hits, 0);
        for (AccessibilityNodeInfo node : hits) {
            if (node.isClickable()) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        }
    }

    private void collect(AccessibilityNodeInfo node, List<AccessibilityNodeInfo> out, int depth) {
        if (node == null || depth > 30 || !out.isEmpty()) {
            return;
        }
        if (matches(node.getText()) || matches(node.getContentDescription())) {
            out.add(node);
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collect(node.getChild(i), out, depth + 1);
        }
    }

    private boolean matches(CharSequence value) {
        if (value == null) {
            return false;
        }
        String text = value.toString().trim();
        if (text.isEmpty() || text.length() > 12) {
            return false;
        }
        for (String word : SKIP_WORDS) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {
    }
}
