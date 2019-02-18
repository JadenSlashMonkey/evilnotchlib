package com.evilnotch.lib.asm.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.ClassNode;

import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.asm.FMLCorePlugin;
import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;

/***
 * ASM tests
 * Copyright (c) 2000-2011 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * A ClassWriter that computes the common super class of two classes without
 * actually loading them with a ClassLoader.
 * 
 * @author Eric Bruneton
 */
public class ComputeClassWriter extends ClassWriter {

    private LaunchClassLoader l = Launch.classLoader;

    public ComputeClassWriter(final int flags) 
    {
        super(flags);
    }
    /**
     * patched to work with minecraft first two lines @author jredfox
     */
    @Override
    protected String getCommonSuperClass(String t1, String t2) 
    {
        try 
        {
        	//mc patch here to work in obfuscated enviorment
        	String type1 = ObfHelper.forceToDeobfClassName(t1.replace('.', '/')).replace('.', '/');
        	String type2 = ObfHelper.forceToDeobfClassName(t2.replace('.', '/')).replace('.', '/');
        	
            ClassReader info1 = typeInfo(type1);
            ClassReader info2 = typeInfo(type2);
            if ((info1.getAccess() & Opcodes.ACC_INTERFACE) != 0) 
            {
                if (typeImplements(type2, info2, type1)) 
                {
                    return type1;
                } 
                else 
                {
                    return "java/lang/Object";
                }
            }
            if ((info2.getAccess() & Opcodes.ACC_INTERFACE) != 0) 
            {
                if (typeImplements(type1, info1, type2)) 
                {
                    return type2;
                } 
                else 
                {
                    return "java/lang/Object";
                }
            }
            StringBuilder b1 = typeAncestors(type1, info1);
            StringBuilder b2 = typeAncestors(type2, info2);
            String result = "java/lang/Object";
            int end1 = b1.length();
            int end2 = b2.length();
            while (true) 
            {
                int start1 = b1.lastIndexOf(";", end1 - 1);
                int start2 = b2.lastIndexOf(";", end2 - 1);
                if (start1 != -1 && start2 != -1 && end1 - start1 == end2 - start2) 
                {
                    String p1 = b1.substring(start1 + 1, end1);
                    String p2 = b2.substring(start2 + 1, end2);
                    if (p1.equals(p2)) 
                    {
                        result = p1;
                        end1 = start1;
                        end2 = start2;
                    } 
                    else 
                    {
                        return result;
                    }
                } 
                else 
                {
                    return result;
                }
            }
        } 
        catch (Exception e) 
        {
        	e.printStackTrace();
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Returns the internal names of the ancestor classes of the given type.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @param info
     *            the ClassReader corresponding to 'type'.
     * @return a StringBuilder containing the ancestor classes of 'type',
     *         separated by ';'. The returned string has the following format:
     *         ";type1;type2 ... ;typeN", where type1 is 'type', and typeN is a
     *         direct subclass of Object. If 'type' is Object, the returned
     *         string is empty.
     * @throws IOException
     *             if the bytecode of 'type' or of some of its ancestor class
     *             cannot be loaded.
     */
    private StringBuilder typeAncestors(String type, ClassReader info) throws Exception 
    {
        StringBuilder b = new StringBuilder();
        while (!"java/lang/Object".equals(type)) 
        {
            b.append(';').append(type);
            type = info.getSuperName();
            info = typeInfo(type);
        }
        return b;
    }

    /**
     * Returns true if the given type implements the given interface.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @param info
     *            the ClassReader corresponding to 'type'.
     * @param itf
     *            the internal name of a interface.
     * @return true if 'type' implements directly or indirectly 'itf'
     * @throws IOException
     *             if the bytecode of 'type' or of some of its ancestor class
     *             cannot be loaded.
     */
    private boolean typeImplements(String type, ClassReader info, String itf) throws Exception 
    {
        while (!"java/lang/Object".equals(type)) 
        {
            String[] itfs = info.getInterfaces();
            for (int i = 0; i < itfs.length; ++i) 
            {
                if (itfs[i].equals(itf)) 
                {
                    return true;
                }
            }
            for (int i = 0; i < itfs.length; ++i) 
            {
                if (typeImplements(itfs[i], typeInfo(itfs[i]), itf)) 
                {
                    return true;
                }
            }
            type = info.getSuperName();
            info = typeInfo(type);
        }
        return false;
    }

    /**
     * Returns a ClassReader corresponding to the given class or interface.
     * 
     * @param type
     *            the internal name of a class or interface.
     * @return the ClassReader corresponding to 'type'.
     * @throws IOException
     *             if the bytecode of 'type' cannot be loaded.
     */
    public static Map<String,ClassReader> byteCache = new ConcurrentHashMap<String,ClassReader>(350); 
    private ClassReader typeInfo(final String t) throws Exception 
    {
    	String deob = ObfHelper.toDeobfClassName(t);
    	byte[] bb = getClassBytes(deob.replace('/', '.'));
    	if(bb != null)
    	{
    		return new ClassReader(bb);
    	}
    	
    	String type = ObfHelper.toObfClassName(t.replace('.', '/')).replace('.', '/');
    	ClassReader reader = null;
    	if(byteCache.containsKey(type))
    	{
    		return byteCache.get(type);
    	}
    	
        InputStream is = l.getResourceAsStream(type + ".class");
        
        try 
        {
            reader = new ClassReader(is);
            if(FMLCorePlugin.isObf)
            	reader = patchClass(reader);
            byteCache.put(type, reader);
            return reader;
        } 
        finally 
        {
            is.close();
        }
    }
    
    public Map<String,byte[]> resourceCache = (Map<String, byte[]>) ReflectionUtil.getObject(l, LaunchClassLoader.class, "resourceCache");
    private byte[] getClassBytes(String deob) 
    {	
    	if(resourceCache.containsKey(deob))
    	{
    		return resourceCache.get(deob);
    	}
    	return null;
	}

	private static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
    private static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
    private static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;
    /**
     * deob the class without loading more classes
     */
    private ClassReader patchClass(ClassReader reader) 
	{
		ClassWriter classWriter = new ClassWriter(WRITER_FLAGS);
        RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
		reader.accept(remapAdapter, READER_FLAGS);
		return new ClassReader(classWriter.toByteArray());
	}
	
	/**
     * is the class currently loaded
     */
    public static Class getClass(String name) throws Exception
    {
        java.lang.reflect.Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] { String.class });
        m.setAccessible(true);
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class test = (Class) m.invoke(cl, name);
        return test;
    }
}