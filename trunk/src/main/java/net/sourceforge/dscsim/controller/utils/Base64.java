/*
 * Created on ??.??.????
 * ???????
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'dscsim'.
 *
 * The Initial Developer of the Original Code is unknown. 
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
 

package net.sourceforge.dscsim.controller.utils;


public class Base64
{

    public Base64()
    {
    }

    public static String encodeAndFormat(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        boolean flag = false;
        for(int i = 0; i < abyte0.length; i += 3)
        {
            if(i != 0 && i % 48 == 0)
                stringbuffer.append("\r\n");
            stringbuffer.append(encodeBlock(abyte0, i));
        }

        return stringbuffer.toString();
    }

    public static String encode(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < abyte0.length; i += 3)
            stringbuffer.append(encodeBlock(abyte0, i));

        return stringbuffer.toString();
    }

    protected static char[] encodeBlock(byte abyte0[], int i)
    {
        int j = 0;
        int k = abyte0.length - i - 1;
        int l = k < 2 ? k : 2;
        for(int i1 = 0; i1 <= l; i1++)
        {
            byte byte0 = abyte0[i + i1];
            int j1 = byte0 >= 0 ? ((int) (byte0)) : byte0 + 256;
            j += j1 << 8 * (2 - i1);
        }

        char ac[] = new char[4];
        for(int k1 = 0; k1 < 4; k1++)
        {
            int l1 = j >>> 6 * (3 - k1) & 0x3f;
            ac[k1] = getChar(l1);
        }

        if(k < 1)
            ac[2] = '=';
        if(k < 2)
            ac[3] = '=';
        return ac;
    }

    protected static char getChar(int i)
    {
        if(i >= 0 && i <= 25)
            return (char)(65 + i);
        if(i >= 26 && i <= 51)
            return (char)(97 + (i - 26));
        if(i >= 52 && i <= 61)
            return (char)(48 + (i - 52));
        if(i == 62)
            return '+';
        return i != 63 ? '?' : '/';
    }

    public static byte[] decode(String s)
    {
        int i = 0;
        for(int j = s.length() - 1; s.charAt(j) == '='; j--)
            i++;

        int k = (s.length() * 6) / 8 - i;
        byte abyte0[] = new byte[k];
        int l = 0;
        for(int i1 = 0; i1 < s.length(); i1 += 4)
        {
            int j1 = (getValue(s.charAt(i1)) << 18) + (getValue(s.charAt(i1 + 1)) << 12) + (getValue(s.charAt(i1 + 2)) << 6) + getValue(s.charAt(i1 + 3));
            for(int k1 = 0; k1 < 3 && l + k1 < abyte0.length; k1++)
                abyte0[l + k1] = (byte)(j1 >> 8 * (2 - k1) & 0xff);

            l += 3;
        }

        return abyte0;
    }

    protected static int getValue(char c)
    {
        if(c >= 'A' && c <= 'Z')
            return c - 65;
        if(c >= 'a' && c <= 'z')
            return (c - 97) + 26;
        if(c >= '0' && c <= '9')
            return (c - 48) + 52;
        if(c == '+')
            return 62;
        if(c == '/')
            return 63;
        return c != '=' ? -1 : 0;
    }
}
