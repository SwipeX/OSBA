package com.bool.util;

import com.bool.asm.ClassNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Jeroen
 * Date: 27-2-13
 * Time: 21:08
 */
public class ClassPath {

    private Map<String, ClassNode> classes = new HashMap<String, ClassNode>();

    public ClassPath() {
    }

    public void addJar(String fileName) throws IOException {
        JarInputStream jis = new JarInputStream(new FileInputStream(fileName));
        JarEntry entry;
        while ((entry = jis.getNextJarEntry()) != null) {
            if (entry.getName().endsWith(".class")) {
                ClassReader cr = new ClassReader(jis);
                ClassNode cn = new ClassNode();
                cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                classes.put(cn.name, cn);
            }
        }
    }

    public Map<String, ClassNode> getClasses() {
        return classes;
    }

    public ClassNode getClass(String name){
        return classes.get(name);
    }

    public void safeJar(String name) throws IOException {
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(name));
        for(ClassNode cn : classes.values()){
            System.out.println("Saving: "+ cn.name);
            JarEntry entry = new JarEntry(cn.name + ".class");
            jos.putNextEntry(entry);
            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);
            jos.write(cw.toByteArray());
            jos.closeEntry();
        }
        jos.close();
    }
}
