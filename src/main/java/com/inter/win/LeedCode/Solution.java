package com.inter.win.LeedCode;

import oracle.security.crypto.core.math.BigInt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Solution {

    public static void main(String[] args) {
        int[] arr1 = {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
        int[] arr2 = {5,6,4};
        Solution solution = new Solution();
        ListNode node1 = solution.createNode(arr1);
        ListNode node2 = solution.createNode(arr2);
        ListNode listNode = solution.addTwoNumbers(node1, node2);
        System.out.println(listNode);
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();
        getNum(l1, list1);
        getNum(l2, list2);
        Collections.reverse(list1);
        Collections.reverse(list2);
        String a = "";
        String b = "";
        for (Integer u : list1) {
            a += u;
        }
        for (Integer u : list2) {
            b += u;
        }
        BigInteger bi1 = new BigInteger(a);
        BigInteger bi2 = new BigInteger(b);
        String res = bi1.multiply(bi2).toString();

        char[] arr = res.toCharArray();
        for (char c : arr) {
            list3.add(Integer.parseInt(c + ""));
        }
        Collections.reverse(list3);
        ListNode temp = null;
        for(int i = list3.size(); i > 0; i--) {
            if (i == list3.size()) {
                temp = new ListNode(list3.get(i - 1));
            } else {
                ListNode temp1 = new ListNode(list3.get(i-1), temp);
                temp = temp1;
            }
        }
        return temp;

    }
    public void getNum(ListNode l, List<Integer> list) {
        list.add(l.val);
        if (l.next != null) {
            getNum(l.next, list);
        } else {
            return;
        }
    }

    public ListNode createNode(int[] arr) {
        ListNode temp = null;
        for(int i = arr.length; i > 0; i--) {
            if (i == arr.length) {
                temp = new ListNode(arr[i - 1]);
            } else {
                ListNode temp1 = new ListNode(arr[i - 1], temp);
                temp = temp1;
            }
        }
        return temp;
    }
}
