package ummisco.gama.opengl.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opengl.GLProfile;

import jogamp.opengl.GLContextImpl;
import jogamp.opengl.gl4.GL4bcImpl;

public class OwnGL extends GL4bcImpl {

	public OwnGL(final GLProfile glp, final GLContextImpl context) {
		super(glp, context);
	}

	@Override
	public void glEnable(final int cap) {
		super.glEnable(cap);
	}

	@Override
	public void glDisable(final int cap) {
		super.glDisable(cap);
	}

	@Override
	public void glMatrixMode(final int mode) {
		super.glMatrixMode(mode);
	}

	@Override
	public void glPushMatrix() {
		super.glPushMatrix();
	}

	@Override
	public void glPopMatrix() {
		super.glPopMatrix();
	}

	@Override
	public void glLoadIdentity() {
		super.glLoadIdentity();
	}

	@Override
	public void glRotated(final double angle, final double x, final double y, final double z) {
		super.glRotated(angle, x, y, z);
	}

	@Override
	public void glRotatef(final float angle, final float x, final float y, final float z) {
		super.glRotatef(angle, x, y, z);
	}

	@Override
	public void glScaled(final double x, final double y, final double z) {
		super.glScaled(x, y, z);
	}

	@Override
	public void glScalef(final float x, final float y, final float z) {
		super.glScalef(x, y, z);
	}

	@Override
	public void glTranslated(final double x, final double y, final double z) {
		super.glTranslated(x, y, z);
	}

	@Override
	public void glTranslatef(final float x, final float y, final float z) {
		super.glTranslatef(x, y, z);
	}

	@Override
	public void glBegin(final int mode) {
		super.glBegin(mode);
	}

	@Override
	public void glEnd() {
		super.glEnd();
	}

	@Override
	public void glVertex2d(final double x, final double y) {
		this.glVertex3d(x, y, 0);
	}

	@Override
	public void glVertex2f(final float x, final float y) {
		this.glVertex3d(x, y, 0f);
	}

	@Override
	public void glVertex2i(final int x, final int y) {
		this.glVertex3d(x, y, 0);
	}

	@Override
	public void glVertex2s(final short x, final short y) {
		this.glVertex3d(x, y, 0);
	}

	@Override
	public void glVertex3d(final double x, final double y, final double z) {
		super.glVertex3d(x, y, z);
	}

	@Override
	public void glVertex3f(final float x, final float y, final float z) {
		this.glVertex3d(x, y, z);
	}

	@Override
	public void glVertex3i(final int x, final int y, final int z) {
		this.glVertex3d(x, y, z);
	}

	@Override
	public void glVertex3s(final short x, final short y, final short z) {
		this.glVertex3d(x, y, z);
	}

	@Override
	public void glNormal3b(final byte nx, final byte ny, final byte nz) {
		this.glNormal3d(nx, ny, nz);
	}

	@Override
	public void glNormal3d(final double nx, final double ny, final double nz) {
		super.glNormal3d(nx, ny, nz);
	}

	@Override
	public void glNormal3f(final float nx, final float ny, final float nz) {
		this.glNormal3d(nx, ny, nz);
	}

	@Override
	public void glNormal3i(final int nx, final int ny, final int nz) {
		this.glNormal3d(nx, ny, nz);
	}

	@Override
	public void glNormal3s(final short nx, final short ny, final short nz) {
		this.glNormal3d(nx, ny, nz);
	}

	@Override
	public void glColor3b(final byte red, final byte green, final byte blue) {
		this.glColor4d(red, green, blue, 1);
	}

	@Override
	public void glColor3d(final double red, final double green, final double blue) {
		this.glColor4d(red, green, blue, 1);
	}

	@Override
	public void glColor3f(final float red, final float green, final float blue) {
		this.glColor4d(red, green, blue, 1);
	}

	@Override
	public void glColor3i(final int red, final int green, final int blue) {
		this.glColor4d(red, green, blue, 1);
	}

	@Override
	public void glColor3s(final short red, final short green, final short blue) {
		this.glColor4d(red, green, blue, 1);
	}

	@Override
	public void glColor4b(final byte red, final byte green, final byte blue, final byte alpha) {
		this.glColor4d(red, green, blue, alpha);
	}

	@Override
	public void glColor4d(final double red, final double green, final double blue, final double alpha) {
		super.glColor4d(red, green, blue, alpha);
	}

	@Override
	public void glColor4f(final float red, final float green, final float blue, final float alpha) {
		this.glColor4d(red, green, blue, alpha);
	}

	@Override
	public void glColor4i(final int red, final int green, final int blue, final int alpha) {
		this.glColor4d(red, green, blue, alpha);
	}

	@Override
	public void glColor4s(final short red, final short green, final short blue, final short alpha) {
		this.glColor4d(red, green, blue, alpha);
	}

	@Override
	public void glTexCoord1d(final double s) {
		this.glTexCoord3d(s, 0, 0);
	}

	@Override
	public void glTexCoord1f(final float s) {
		this.glTexCoord3d(s, 0, 0);
	}

	@Override
	public void glTexCoord1i(final int s) {
		this.glTexCoord3d(s, 0, 0);
	}

	@Override
	public void glTexCoord1s(final short s) {
		this.glTexCoord3d(s, 0, 0);
	}

	@Override
	public void glTexCoord2d(final double s, final double t) {
		this.glTexCoord3d(s, t, 0);
	}

	@Override
	public void glTexCoord2f(final float s, final float t) {
		this.glTexCoord3d(s, t, 0);
	}

	@Override
	public void glTexCoord2i(final int s, final int t) {
		this.glTexCoord3d(s, t, 0);
	}

	@Override
	public void glTexCoord2s(final short s, final short t) {
		this.glTexCoord3d(s, t, 0);
	}

	@Override
	public void glTexCoord3d(final double s, final double t, final double r) {
		super.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord3f(final float s, final float t, final float r) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord3i(final int s, final int t, final int r) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord3s(final short s, final short t, final short r) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord4d(final double s, final double t, final double r, final double q) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord4f(final float s, final float t, final float r, final float q) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord4i(final int s, final int t, final int r, final int q) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glTexCoord4s(final short s, final short t, final short r, final short q) {
		this.glTexCoord3d(s, t, r);
	}

	@Override
	public void glRasterPos2d(final double x, final double y) {
		super.glRasterPos2d(x, y);
	}

	@Override
	public void glRasterPos2f(final float x, final float y) {
		super.glRasterPos2f(x, y);
	}

	@Override
	public void glRasterPos2i(final int x, final int y) {
		super.glRasterPos2i(x, y);
	}

	@Override
	public void glRasterPos2s(final short x, final short y) {
		super.glRasterPos2s(x, y);
	}

	@Override
	public void glRasterPos3d(final double x, final double y, final double z) {
		super.glRasterPos3d(x, y, z);
	}

	@Override
	public void glRasterPos3f(final float x, final float y, final float z) {
		super.glRasterPos3f(x, y, z);
	}

	@Override
	public void glRasterPos3i(final int x, final int y, final int z) {
		super.glRasterPos3i(x, y, z);
	}

	@Override
	public void glRasterPos3s(final short x, final short y, final short z) {
		super.glRasterPos3s(x, y, z);
	}

	@Override
	public void glRasterPos4d(final double x, final double y, final double z, final double w) {
		super.glRasterPos4d(x, y, z, w);
	}

	@Override
	public void glRasterPos4f(final float x, final float y, final float z, final float w) {
		super.glRasterPos4f(x, y, z, w);
	}

	@Override
	public void glRasterPos4i(final int x, final int y, final int z, final int w) {
		super.glRasterPos4i(x, y, z, w);
	}

	@Override
	public void glRasterPos4s(final short x, final short y, final short z, final short w) {
		super.glRasterPos4s(x, y, z, w);
	}

	@Override
	public void glRasterPos2dv(final DoubleBuffer v) {
		super.glRasterPos2dv(v);
	}

	@Override
	public void glRasterPos2dv(final double[] v, final int v_offset) {
		super.glRasterPos2dv(v, v_offset);
	}

	@Override
	public void glRasterPos2fv(final FloatBuffer v) {
		super.glRasterPos2fv(v);
	}

	@Override
	public void glRasterPos2fv(final float[] v, final int v_offset) {
		super.glRasterPos2fv(v, v_offset);
	}

	@Override
	public void glRasterPos2iv(final IntBuffer v) {
		super.glRasterPos2iv(v);
	}

	@Override
	public void glRasterPos2iv(final int[] v, final int v_offset) {
		super.glRasterPos2iv(v, v_offset);
	}

	@Override
	public void glRasterPos2sv(final ShortBuffer v) {
		super.glRasterPos2sv(v);
	}

	@Override
	public void glRasterPos2sv(final short[] v, final int v_offset) {
		super.glRasterPos2sv(v, v_offset);
	}

	@Override
	public void glRasterPos3dv(final DoubleBuffer v) {
		super.glRasterPos3dv(v);
	}

	@Override
	public void glRasterPos3dv(final double[] v, final int v_offset) {
		super.glRasterPos3dv(v, v_offset);
	}

	@Override
	public void glRasterPos3fv(final FloatBuffer v) {
		super.glRasterPos3fv(v);
	}

	@Override
	public void glRasterPos3fv(final float[] v, final int v_offset) {
		super.glRasterPos3fv(v, v_offset);
	}

	@Override
	public void glRasterPos3iv(final IntBuffer v) {
		super.glRasterPos3iv(v);
	}

	@Override
	public void glRasterPos3iv(final int[] v, final int v_offset) {
		super.glRasterPos3iv(v, v_offset);
	}

	@Override
	public void glRasterPos3sv(final ShortBuffer v) {
		super.glRasterPos3sv(v);
	}

	@Override
	public void glRasterPos3sv(final short[] v, final int v_offset) {
		super.glRasterPos3sv(v, v_offset);
	}

	@Override
	public void glRasterPos4dv(final DoubleBuffer v) {
		super.glRasterPos4dv(v);
	}

	@Override
	public void glRasterPos4dv(final double[] v, final int v_offset) {
		super.glRasterPos4dv(v, v_offset);
	}

	@Override
	public void glRasterPos4fv(final FloatBuffer v) {
		super.glRasterPos4fv(v);
	}

	@Override
	public void glRasterPos4fv(final float[] v, final int v_offset) {
		super.glRasterPos4fv(v, v_offset);
	}

	@Override
	public void glRasterPos4iv(final IntBuffer v) {
		super.glRasterPos4iv(v);
	}

	@Override
	public void glRasterPos4iv(final int[] v, final int v_offset) {
		super.glRasterPos4iv(v, v_offset);
	}

	@Override
	public void glRasterPos4sv(final ShortBuffer v) {
		super.glRasterPos4sv(v);
	}

	@Override
	public void glRasterPos4sv(final short[] v, final int v_offset) {
		super.glRasterPos4sv(v, v_offset);
	}

	@Override
	public void glInitNames() {
		super.glInitNames();
	}

	@Override
	public void glLoadName(final int name) {
		super.glLoadName(name);
	}

	@Override
	public void glPushName(final int name) {
		super.glPushName(name);
	}

	@Override
	public void glPopName() {
		super.glPopName();
	}

	@Override
	public void glIndexub(final byte c) {
		super.glIndexub(c);
	}

	@Override
	public void glIndexubv(final ByteBuffer c) {
		super.glIndexubv(c);
	}

	@Override
	public void glIndexubv(final byte[] c, final int c_offset) {
		super.glIndexubv(c, c_offset);
	}

	@Override
	public void glPushClientAttrib(final int mask) {
		super.glPushClientAttrib(mask);
	}

	@Override
	public void glPopClientAttrib() {
		super.glPopClientAttrib();
	}

	@Override
	public void glEnableClientState(final int cap) {
		super.glEnableClientState(cap);
	}

	@Override
	public void glDisableClientState(final int cap) {
		super.glDisableClientState(cap);
	}

	@Override
	public void glVertexPointer(final int size, final int type, final int stride, final Buffer ptr) {
		super.glVertexPointer(size, type, stride, ptr);
	}

	@Override
	public void glVertexPointer(final int size, final int type, final int stride, final long ptr_buffer_offset) {
		super.glVertexPointer(size, type, stride, ptr_buffer_offset);
	}

	@Override
	public void glNormalPointer(final int type, final int stride, final Buffer ptr) {
		super.glNormalPointer(type, stride, ptr);
	}

	@Override
	public void glNormalPointer(final int type, final int stride, final long ptr_buffer_offset) {
		super.glNormalPointer(type, stride, ptr_buffer_offset);
	}

	@Override
	public void glColorPointer(final int size, final int type, final int stride, final Buffer ptr) {
		super.glColorPointer(size, type, stride, ptr);
	}

	@Override
	public void glColorPointer(final int size, final int type, final int stride, final long ptr_buffer_offset) {
		super.glColorPointer(size, type, stride, ptr_buffer_offset);
	}

	@Override
	public void glIndexPointer(final int type, final int stride, final Buffer ptr) {
		super.glIndexPointer(type, stride, ptr);
	}

	@Override
	public void glTexCoordPointer(final int size, final int type, final int stride, final Buffer ptr) {
		super.glTexCoordPointer(size, type, stride, ptr);
	}

	@Override
	public void glTexCoordPointer(final int size, final int type, final int stride, final long ptr_buffer_offset) {
		super.glTexCoordPointer(size, type, stride, ptr_buffer_offset);
	}

	@Override
	public void glEdgeFlagPointer(final int stride, final Buffer ptr) {
		super.glEdgeFlagPointer(stride, ptr);
	}

	@Override
	public void glEdgeFlagPointer(final int stride, final long ptr_buffer_offset) {
		super.glEdgeFlagPointer(stride, ptr_buffer_offset);
	}

	@Override
	public void glArrayElement(final int i) {
		super.glArrayElement(i);
	}

	@Override
	public void glDrawArrays(final int mode, final int first, final int count) {
		super.glDrawArrays(mode, first, count);
	}

	@Override
	public void glDrawElements(final int mode, final int count, final int type, final Buffer indices) {
		super.glDrawElements(mode, count, type, indices);
	}

	@Override
	public void glDrawElements(final int mode, final int count, final int type, final long indices_buffer_offset) {
		super.glDrawElements(mode, count, type, indices_buffer_offset);
	}

	@Override
	public void glInterleavedArrays(final int format, final int stride, final Buffer pointer) {
		super.glInterleavedArrays(format, stride, pointer);
	}

	@Override
	public void glInterleavedArrays(final int format, final int stride, final long pointer_buffer_offset) {
		super.glInterleavedArrays(format, stride, pointer_buffer_offset);
	}

	@Override
	public void glGenTextures(final int n, final IntBuffer textures) {
		super.glGenTextures(n, textures);
	}

	@Override
	public void glGenTextures(final int n, final int[] textures, final int textures_offset) {
		super.glGenTextures(n, textures, textures_offset);
	}

	@Override
	public void glDeleteTextures(final int n, final IntBuffer textures) {
		super.glDeleteTextures(n, textures);
	}

	@Override
	public void glDeleteTextures(final int n, final int[] textures, final int textures_offset) {
		super.glDeleteTextures(n, textures, textures_offset);
	}

	@Override
	public void glBindTexture(final int target, final int texture) {
		super.glBindTexture(target, texture);
	}

	@Override
	public void glPrioritizeTextures(final int n, final IntBuffer textures, final FloatBuffer priorities) {
		super.glPrioritizeTextures(n, textures, priorities);
	}

	@Override
	public void glPrioritizeTextures(final int n, final int[] textures, final int textures_offset,
			final float[] priorities, final int priorities_offset) {
		super.glPrioritizeTextures(n, textures, textures_offset, priorities, priorities_offset);
	}

	@Override
	public boolean glAreTexturesResident(final int n, final IntBuffer textures, final ByteBuffer residences) {
		return super.glAreTexturesResident(n, textures, residences);
	}

	@Override
	public boolean glAreTexturesResident(final int n, final int[] textures, final int textures_offset,
			final byte[] residences, final int residences_offset) {
		return super.glAreTexturesResident(n, textures, textures_offset, residences, residences_offset);
	}

	@Override
	public boolean glIsTexture(final int texture) {
		return super.glIsTexture(texture);
	}

	@Override
	public void glTexSubImage1D(final int target, final int level, final int xoffset, final int width, final int format,
			final int type, final Buffer pixels) {
		super.glTexSubImage1D(target, level, xoffset, width, format, type, pixels);
	}

	@Override
	public void glTexSubImage1D(final int target, final int level, final int xoffset, final int width, final int format,
			final int type, final long pixels_buffer_offset) {
		super.glTexSubImage1D(target, level, xoffset, width, format, type, pixels_buffer_offset);
	}

	@Override
	public void glTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int type, final Buffer pixels) {
		super.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int type, final long pixels_buffer_offset) {
		super.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels_buffer_offset);
	}

	@Override
	public void glCopyTexImage1D(final int target, final int level, final int internalformat, final int x, final int y,
			final int width, final int border) {
		super.glCopyTexImage1D(target, level, internalformat, x, y, width, border);
	}

	@Override
	public void glCopyTexImage2D(final int target, final int level, final int internalformat, final int x, final int y,
			final int width, final int height, final int border) {
		super.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyTexSubImage1D(final int target, final int level, final int xoffset, final int x, final int y,
			final int width) {
		super.glCopyTexSubImage1D(target, level, xoffset, x, y, width);
	}

	@Override
	public void glCopyTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset,
			final int x, final int y, final int width, final int height) {
		super.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glTexStorage1D(final int target, final int levels, final int internalformat, final int width) {
		super.glTexStorage1D(target, levels, internalformat, width);
	}

	@Override
	public void glTexStorage2D(final int target, final int levels, final int internalformat, final int width,
			final int height) {
		super.glTexStorage2D(target, levels, internalformat, width, height);
	}

	@Override
	public void glTexStorage3D(final int target, final int levels, final int internalformat, final int width,
			final int height, final int depth) {
		super.glTexStorage3D(target, levels, internalformat, width, height, depth);
	}

	@Override
	public void glTextureStorage1DEXT(final int texture, final int target, final int levels, final int internalformat,
			final int width) {
		super.glTextureStorage1DEXT(texture, target, levels, internalformat, width);
	}

	@Override
	public void glTextureStorage2DEXT(final int texture, final int target, final int levels, final int internalformat,
			final int width, final int height) {
		super.glTextureStorage2DEXT(texture, target, levels, internalformat, width, height);
	}

	@Override
	public void glTextureStorage3DEXT(final int texture, final int target, final int levels, final int internalformat,
			final int width, final int height, final int depth) {
		super.glTextureStorage3DEXT(texture, target, levels, internalformat, width, height, depth);
	}

	@Override
	public void glActiveShaderProgram(final int pipeline, final int program) {
		super.glActiveShaderProgram(pipeline, program);
	}

	@Override
	public void glBindProgramPipeline(final int pipeline) {
		super.glBindProgramPipeline(pipeline);
	}

	@Override
	public int glCreateShaderProgramv(final int type, final int count, final String[] strings) {
		return super.glCreateShaderProgramv(type, count, strings);
	}

	@Override
	public void glDeleteProgramPipelines(final int n, final IntBuffer pipelines) {
		super.glDeleteProgramPipelines(n, pipelines);
	}

	@Override
	public void glDeleteProgramPipelines(final int n, final int[] pipelines, final int pipelines_offset) {
		super.glDeleteProgramPipelines(n, pipelines, pipelines_offset);
	}

	@Override
	public void glGenProgramPipelines(final int n, final IntBuffer pipelines) {
		super.glGenProgramPipelines(n, pipelines);
	}

	@Override
	public void glGenProgramPipelines(final int n, final int[] pipelines, final int pipelines_offset) {
		super.glGenProgramPipelines(n, pipelines, pipelines_offset);
	}

	@Override
	public void glGetProgramPipelineInfoLog(final int pipeline, final int bufSize, final IntBuffer length,
			final ByteBuffer infoLog) {
		super.glGetProgramPipelineInfoLog(pipeline, bufSize, length, infoLog);
	}

	@Override
	public void glGetProgramPipelineInfoLog(final int pipeline, final int bufSize, final int[] length,
			final int length_offset, final byte[] infoLog, final int infoLog_offset) {
		super.glGetProgramPipelineInfoLog(pipeline, bufSize, length, length_offset, infoLog, infoLog_offset);
	}

	@Override
	public void glGetProgramPipelineiv(final int pipeline, final int pname, final IntBuffer params) {
		super.glGetProgramPipelineiv(pipeline, pname, params);
	}

	@Override
	public void glGetProgramPipelineiv(final int pipeline, final int pname, final int[] params,
			final int params_offset) {
		super.glGetProgramPipelineiv(pipeline, pname, params, params_offset);
	}

	@Override
	public boolean glIsProgramPipeline(final int pipeline) {
		return super.glIsProgramPipeline(pipeline);
	}

	@Override
	public void glProgramParameteri(final int program, final int pname, final int value) {
		super.glProgramParameteri(program, pname, value);
	}

	@Override
	public void glProgramUniform1f(final int program, final int location, final float v0) {
		super.glProgramUniform1f(program, location, v0);
	}

	@Override
	public void glProgramUniform1fv(final int program, final int location, final int count, final FloatBuffer value) {
		super.glProgramUniform1fv(program, location, count, value);
	}

	@Override
	public void glProgramUniform1fv(final int program, final int location, final int count, final float[] value,
			final int value_offset) {
		super.glProgramUniform1fv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform1i(final int program, final int location, final int v0) {
		super.glProgramUniform1i(program, location, v0);
	}

	@Override
	public void glProgramUniform1iv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform1iv(program, location, count, value);
	}

	@Override
	public void glProgramUniform1iv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform1iv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2f(final int program, final int location, final float v0, final float v1) {
		super.glProgramUniform2f(program, location, v0, v1);
	}

	@Override
	public void glProgramUniform2fv(final int program, final int location, final int count, final FloatBuffer value) {
		super.glProgramUniform2fv(program, location, count, value);
	}

	@Override
	public void glProgramUniform2fv(final int program, final int location, final int count, final float[] value,
			final int value_offset) {
		super.glProgramUniform2fv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2i(final int program, final int location, final int v0, final int v1) {
		super.glProgramUniform2i(program, location, v0, v1);
	}

	@Override
	public void glProgramUniform2iv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform2iv(program, location, count, value);
	}

	@Override
	public void glProgramUniform2iv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform2iv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3f(final int program, final int location, final float v0, final float v1,
			final float v2) {
		super.glProgramUniform3f(program, location, v0, v1, v2);
	}

	@Override
	public void glProgramUniform3fv(final int program, final int location, final int count, final FloatBuffer value) {
		super.glProgramUniform3fv(program, location, count, value);
	}

	@Override
	public void glProgramUniform3fv(final int program, final int location, final int count, final float[] value,
			final int value_offset) {
		super.glProgramUniform3fv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3i(final int program, final int location, final int v0, final int v1, final int v2) {
		super.glProgramUniform3i(program, location, v0, v1, v2);
	}

	@Override
	public void glProgramUniform3iv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform3iv(program, location, count, value);
	}

	@Override
	public void glProgramUniform3iv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform3iv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4f(final int program, final int location, final float v0, final float v1,
			final float v2, final float v3) {
		super.glProgramUniform4f(program, location, v0, v1, v2, v3);
	}

	@Override
	public void glProgramUniform4fv(final int program, final int location, final int count, final FloatBuffer value) {
		super.glProgramUniform4fv(program, location, count, value);
	}

	@Override
	public void glProgramUniform4fv(final int program, final int location, final int count, final float[] value,
			final int value_offset) {
		super.glProgramUniform4fv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4i(final int program, final int location, final int v0, final int v1, final int v2,
			final int v3) {
		super.glProgramUniform4i(program, location, v0, v1, v2, v3);
	}

	@Override
	public void glProgramUniform4iv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform4iv(program, location, count, value);
	}

	@Override
	public void glProgramUniform4iv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform4iv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix2fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix2fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix3fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix3fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix4fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix4fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glUseProgramStages(final int pipeline, final int stages, final int program) {
		super.glUseProgramStages(pipeline, stages, program);
	}

	@Override
	public void glValidateProgramPipeline(final int pipeline) {
		super.glValidateProgramPipeline(pipeline);
	}

	@Override
	public void glProgramUniform1ui(final int program, final int location, final int v0) {
		super.glProgramUniform1ui(program, location, v0);
	}

	@Override
	public void glProgramUniform2ui(final int program, final int location, final int v0, final int v1) {
		super.glProgramUniform2ui(program, location, v0, v1);
	}

	@Override
	public void glProgramUniform3ui(final int program, final int location, final int v0, final int v1, final int v2) {
		super.glProgramUniform3ui(program, location, v0, v1, v2);
	}

	@Override
	public void glProgramUniform4ui(final int program, final int location, final int v0, final int v1, final int v2,
			final int v3) {
		super.glProgramUniform4ui(program, location, v0, v1, v2, v3);
	}

	@Override
	public void glProgramUniform1uiv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform1uiv(program, location, count, value);
	}

	@Override
	public void glProgramUniform1uiv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform1uiv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2uiv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform2uiv(program, location, count, value);
	}

	@Override
	public void glProgramUniform2uiv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform2uiv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3uiv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform3uiv(program, location, count, value);
	}

	@Override
	public void glProgramUniform3uiv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform3uiv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4uiv(final int program, final int location, final int count, final IntBuffer value) {
		super.glProgramUniform4uiv(program, location, count, value);
	}

	@Override
	public void glProgramUniform4uiv(final int program, final int location, final int count, final int[] value,
			final int value_offset) {
		super.glProgramUniform4uiv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x3fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix2x3fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x3fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix2x3fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x2fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix3x2fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x2fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix3x2fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x4fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix2x4fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x4fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix2x4fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x2fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix4x2fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x2fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix4x2fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x4fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix3x4fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x4fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix3x4fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x3fv(final int program, final int location, final int count,
			final boolean transpose, final FloatBuffer value) {
		super.glProgramUniformMatrix4x3fv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x3fv(final int program, final int location, final int count,
			final boolean transpose, final float[] value, final int value_offset) {
		super.glProgramUniformMatrix4x3fv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glDrawRangeElements(final int mode, final int start, final int end, final int count, final int type,
			final Buffer indices) {
		super.glDrawRangeElements(mode, start, end, count, type, indices);
	}

	@Override
	public void glDrawRangeElements(final int mode, final int start, final int end, final int count, final int type,
			final long indices_buffer_offset) {
		super.glDrawRangeElements(mode, start, end, count, type, indices_buffer_offset);
	}

	@Override
	public void glTexImage3D(final int target, final int level, final int internalformat, final int width,
			final int height, final int depth, final int border, final int format, final int type,
			final Buffer pixels) {
		super.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
	}

	@Override
	public void glTexImage3D(final int target, final int level, final int internalformat, final int width,
			final int height, final int depth, final int border, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTexImage3D(target, level, internalformat, width, height, depth, border, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final Buffer pixels) {
		super.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
	}

	@Override
	public void glTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glCopyTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int x, final int y, final int width, final int height) {
		super.glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
	}

	@Override
	public void glActiveTexture(final int texture) {
		super.glActiveTexture(texture);
	}

	@Override
	public void glSampleCoverage(final float value, final boolean invert) {
		super.glSampleCoverage(value, invert);
	}

	@Override
	public void glCompressedTexImage3D(final int target, final int level, final int internalformat, final int width,
			final int height, final int depth, final int border, final int imageSize, final Buffer data) {
		super.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize, data);
	}

	@Override
	public void glCompressedTexImage3D(final int target, final int level, final int internalformat, final int width,
			final int height, final int depth, final int border, final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexImage3D(target, level, internalformat, width, height, depth, border, imageSize,
				data_buffer_offset);
	}

	@Override
	public void glCompressedTexImage2D(final int target, final int level, final int internalformat, final int width,
			final int height, final int border, final int imageSize, final Buffer data) {
		super.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
	}

	@Override
	public void glCompressedTexImage2D(final int target, final int level, final int internalformat, final int width,
			final int height, final int border, final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize,
				data_buffer_offset);
	}

	@Override
	public void glCompressedTexImage1D(final int target, final int level, final int internalformat, final int width,
			final int border, final int imageSize, final Buffer data) {
		super.glCompressedTexImage1D(target, level, internalformat, width, border, imageSize, data);
	}

	@Override
	public void glCompressedTexImage1D(final int target, final int level, final int internalformat, final int width,
			final int border, final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexImage1D(target, level, internalformat, width, border, imageSize, data_buffer_offset);
	}

	@Override
	public void glCompressedTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format,
			final int imageSize, final Buffer data) {
		super.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format,
				imageSize, data);
	}

	@Override
	public void glCompressedTexSubImage3D(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format,
			final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format,
				imageSize, data_buffer_offset);
	}

	@Override
	public void glCompressedTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int imageSize, final Buffer data) {
		super.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
	}

	@Override
	public void glCompressedTexSubImage2D(final int target, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize,
				data_buffer_offset);
	}

	@Override
	public void glCompressedTexSubImage1D(final int target, final int level, final int xoffset, final int width,
			final int format, final int imageSize, final Buffer data) {
		super.glCompressedTexSubImage1D(target, level, xoffset, width, format, imageSize, data);
	}

	@Override
	public void glCompressedTexSubImage1D(final int target, final int level, final int xoffset, final int width,
			final int format, final int imageSize, final long data_buffer_offset) {
		super.glCompressedTexSubImage1D(target, level, xoffset, width, format, imageSize, data_buffer_offset);
	}

	@Override
	public void glGetCompressedTexImage(final int target, final int level, final Buffer img) {
		super.glGetCompressedTexImage(target, level, img);
	}

	@Override
	public void glGetCompressedTexImage(final int target, final int level, final long img_buffer_offset) {
		super.glGetCompressedTexImage(target, level, img_buffer_offset);
	}

	@Override
	public void glClientActiveTexture(final int texture) {
		super.glClientActiveTexture(texture);
	}

	@Override
	public void glMultiTexCoord1d(final int target, final double s) {
		super.glMultiTexCoord1d(target, s);
	}

	@Override
	public void glMultiTexCoord1dv(final int target, final DoubleBuffer v) {
		super.glMultiTexCoord1dv(target, v);
	}

	@Override
	public void glMultiTexCoord1dv(final int target, final double[] v, final int v_offset) {
		super.glMultiTexCoord1dv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord1f(final int target, final float s) {
		super.glMultiTexCoord1f(target, s);
	}

	@Override
	public void glMultiTexCoord1fv(final int target, final FloatBuffer v) {
		super.glMultiTexCoord1fv(target, v);
	}

	@Override
	public void glMultiTexCoord1fv(final int target, final float[] v, final int v_offset) {
		super.glMultiTexCoord1fv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord1i(final int target, final int s) {
		super.glMultiTexCoord1i(target, s);
	}

	@Override
	public void glMultiTexCoord1iv(final int target, final IntBuffer v) {
		super.glMultiTexCoord1iv(target, v);
	}

	@Override
	public void glMultiTexCoord1iv(final int target, final int[] v, final int v_offset) {
		super.glMultiTexCoord1iv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord1s(final int target, final short s) {
		super.glMultiTexCoord1s(target, s);
	}

	@Override
	public void glMultiTexCoord1sv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord1sv(target, v);
	}

	@Override
	public void glMultiTexCoord1sv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord1sv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord2d(final int target, final double s, final double t) {
		super.glMultiTexCoord2d(target, s, t);
	}

	@Override
	public void glMultiTexCoord2dv(final int target, final DoubleBuffer v) {
		super.glMultiTexCoord2dv(target, v);
	}

	@Override
	public void glMultiTexCoord2dv(final int target, final double[] v, final int v_offset) {
		super.glMultiTexCoord2dv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord2f(final int target, final float s, final float t) {
		super.glMultiTexCoord2f(target, s, t);
	}

	@Override
	public void glMultiTexCoord2fv(final int target, final FloatBuffer v) {
		super.glMultiTexCoord2fv(target, v);
	}

	@Override
	public void glMultiTexCoord2fv(final int target, final float[] v, final int v_offset) {
		super.glMultiTexCoord2fv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord2i(final int target, final int s, final int t) {
		super.glMultiTexCoord2i(target, s, t);
	}

	@Override
	public void glMultiTexCoord2iv(final int target, final IntBuffer v) {
		super.glMultiTexCoord2iv(target, v);
	}

	@Override
	public void glMultiTexCoord2iv(final int target, final int[] v, final int v_offset) {
		super.glMultiTexCoord2iv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord2s(final int target, final short s, final short t) {
		super.glMultiTexCoord2s(target, s, t);
	}

	@Override
	public void glMultiTexCoord2sv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord2sv(target, v);
	}

	@Override
	public void glMultiTexCoord2sv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord2sv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord3d(final int target, final double s, final double t, final double r) {
		super.glMultiTexCoord3d(target, s, t, r);
	}

	@Override
	public void glMultiTexCoord3dv(final int target, final DoubleBuffer v) {
		super.glMultiTexCoord3dv(target, v);
	}

	@Override
	public void glMultiTexCoord3dv(final int target, final double[] v, final int v_offset) {
		super.glMultiTexCoord3dv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord3f(final int target, final float s, final float t, final float r) {
		super.glMultiTexCoord3f(target, s, t, r);
	}

	@Override
	public void glMultiTexCoord3fv(final int target, final FloatBuffer v) {
		super.glMultiTexCoord3fv(target, v);
	}

	@Override
	public void glMultiTexCoord3fv(final int target, final float[] v, final int v_offset) {
		super.glMultiTexCoord3fv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord3i(final int target, final int s, final int t, final int r) {
		super.glMultiTexCoord3i(target, s, t, r);
	}

	@Override
	public void glMultiTexCoord3iv(final int target, final IntBuffer v) {
		super.glMultiTexCoord3iv(target, v);
	}

	@Override
	public void glMultiTexCoord3iv(final int target, final int[] v, final int v_offset) {
		super.glMultiTexCoord3iv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord3s(final int target, final short s, final short t, final short r) {
		super.glMultiTexCoord3s(target, s, t, r);
	}

	@Override
	public void glMultiTexCoord3sv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord3sv(target, v);
	}

	@Override
	public void glMultiTexCoord3sv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord3sv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord4d(final int target, final double s, final double t, final double r, final double q) {
		super.glMultiTexCoord4d(target, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4dv(final int target, final DoubleBuffer v) {
		super.glMultiTexCoord4dv(target, v);
	}

	@Override
	public void glMultiTexCoord4dv(final int target, final double[] v, final int v_offset) {
		super.glMultiTexCoord4dv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord4f(final int target, final float s, final float t, final float r, final float q) {
		super.glMultiTexCoord4f(target, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4fv(final int target, final FloatBuffer v) {
		super.glMultiTexCoord4fv(target, v);
	}

	@Override
	public void glMultiTexCoord4fv(final int target, final float[] v, final int v_offset) {
		super.glMultiTexCoord4fv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord4i(final int target, final int s, final int t, final int r, final int q) {
		super.glMultiTexCoord4i(target, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4iv(final int target, final IntBuffer v) {
		super.glMultiTexCoord4iv(target, v);
	}

	@Override
	public void glMultiTexCoord4iv(final int target, final int[] v, final int v_offset) {
		super.glMultiTexCoord4iv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord4s(final int target, final short s, final short t, final short r, final short q) {
		super.glMultiTexCoord4s(target, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4sv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord4sv(target, v);
	}

	@Override
	public void glMultiTexCoord4sv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord4sv(target, v, v_offset);
	}

	@Override
	public void glLoadTransposeMatrixf(final FloatBuffer m) {
		super.glLoadTransposeMatrixf(m);
	}

	@Override
	public void glLoadTransposeMatrixf(final float[] m, final int m_offset) {
		super.glLoadTransposeMatrixf(m, m_offset);
	}

	@Override
	public void glLoadTransposeMatrixd(final DoubleBuffer m) {
		super.glLoadTransposeMatrixd(m);
	}

	@Override
	public void glLoadTransposeMatrixd(final double[] m, final int m_offset) {
		super.glLoadTransposeMatrixd(m, m_offset);
	}

	@Override
	public void glMultTransposeMatrixf(final FloatBuffer m) {
		super.glMultTransposeMatrixf(m);
	}

	@Override
	public void glMultTransposeMatrixf(final float[] m, final int m_offset) {
		super.glMultTransposeMatrixf(m, m_offset);
	}

	@Override
	public void glMultTransposeMatrixd(final DoubleBuffer m) {
		super.glMultTransposeMatrixd(m);
	}

	@Override
	public void glMultTransposeMatrixd(final double[] m, final int m_offset) {
		super.glMultTransposeMatrixd(m, m_offset);
	}

	@Override
	public void glBlendFuncSeparate(final int sfactorRGB, final int dfactorRGB, final int sfactorAlpha,
			final int dfactorAlpha) {
		super.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	@Override
	public void glMultiDrawArrays(final int mode, final IntBuffer first, final IntBuffer count, final int drawcount) {
		super.glMultiDrawArrays(mode, first, count, drawcount);
	}

	@Override
	public void glMultiDrawArrays(final int mode, final int[] first, final int first_offset, final int[] count,
			final int count_offset, final int drawcount) {
		super.glMultiDrawArrays(mode, first, first_offset, count, count_offset, drawcount);
	}

	@Override
	public void glMultiDrawElements(final int mode, final IntBuffer count, final int type, final PointerBuffer indices,
			final int drawcount) {
		super.glMultiDrawElements(mode, count, type, indices, drawcount);
	}

	@Override
	public void glPointParameterf(final int pname, final float param) {
		super.glPointParameterf(pname, param);
	}

	@Override
	public void glPointParameterfv(final int pname, final FloatBuffer params) {
		super.glPointParameterfv(pname, params);
	}

	@Override
	public void glPointParameterfv(final int pname, final float[] params, final int params_offset) {
		super.glPointParameterfv(pname, params, params_offset);
	}

	@Override
	public void glPointParameteri(final int pname, final int param) {
		super.glPointParameteri(pname, param);
	}

	@Override
	public void glPointParameteriv(final int pname, final IntBuffer params) {
		super.glPointParameteriv(pname, params);
	}

	@Override
	public void glPointParameteriv(final int pname, final int[] params, final int params_offset) {
		super.glPointParameteriv(pname, params, params_offset);
	}

	@Override
	public void glFogCoordf(final float coord) {
		super.glFogCoordf(coord);
	}

	@Override
	public void glFogCoordfv(final FloatBuffer coord) {
		super.glFogCoordfv(coord);
	}

	@Override
	public void glFogCoordfv(final float[] coord, final int coord_offset) {
		super.glFogCoordfv(coord, coord_offset);
	}

	@Override
	public void glFogCoordd(final double coord) {
		super.glFogCoordd(coord);
	}

	@Override
	public void glFogCoorddv(final DoubleBuffer coord) {
		super.glFogCoorddv(coord);
	}

	@Override
	public void glFogCoorddv(final double[] coord, final int coord_offset) {
		super.glFogCoorddv(coord, coord_offset);
	}

	@Override
	public void glFogCoordPointer(final int type, final int stride, final Buffer pointer) {
		super.glFogCoordPointer(type, stride, pointer);
	}

	@Override
	public void glFogCoordPointer(final int type, final int stride, final long pointer_buffer_offset) {
		super.glFogCoordPointer(type, stride, pointer_buffer_offset);
	}

	@Override
	public void glSecondaryColor3b(final byte red, final byte green, final byte blue) {
		super.glSecondaryColor3b(red, green, blue);
	}

	@Override
	public void glSecondaryColor3bv(final ByteBuffer v) {
		super.glSecondaryColor3bv(v);
	}

	@Override
	public void glSecondaryColor3bv(final byte[] v, final int v_offset) {
		super.glSecondaryColor3bv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3d(final double red, final double green, final double blue) {
		super.glSecondaryColor3d(red, green, blue);
	}

	@Override
	public void glSecondaryColor3dv(final DoubleBuffer v) {
		super.glSecondaryColor3dv(v);
	}

	@Override
	public void glSecondaryColor3dv(final double[] v, final int v_offset) {
		super.glSecondaryColor3dv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3f(final float red, final float green, final float blue) {
		super.glSecondaryColor3f(red, green, blue);
	}

	@Override
	public void glSecondaryColor3fv(final FloatBuffer v) {
		super.glSecondaryColor3fv(v);
	}

	@Override
	public void glSecondaryColor3fv(final float[] v, final int v_offset) {
		super.glSecondaryColor3fv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3i(final int red, final int green, final int blue) {
		super.glSecondaryColor3i(red, green, blue);
	}

	@Override
	public void glSecondaryColor3iv(final IntBuffer v) {
		super.glSecondaryColor3iv(v);
	}

	@Override
	public void glSecondaryColor3iv(final int[] v, final int v_offset) {
		super.glSecondaryColor3iv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3s(final short red, final short green, final short blue) {
		super.glSecondaryColor3s(red, green, blue);
	}

	@Override
	public void glSecondaryColor3sv(final ShortBuffer v) {
		super.glSecondaryColor3sv(v);
	}

	@Override
	public void glSecondaryColor3sv(final short[] v, final int v_offset) {
		super.glSecondaryColor3sv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3ub(final byte red, final byte green, final byte blue) {
		super.glSecondaryColor3ub(red, green, blue);
	}

	@Override
	public void glSecondaryColor3ubv(final ByteBuffer v) {
		super.glSecondaryColor3ubv(v);
	}

	@Override
	public void glSecondaryColor3ubv(final byte[] v, final int v_offset) {
		super.glSecondaryColor3ubv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3ui(final int red, final int green, final int blue) {
		super.glSecondaryColor3ui(red, green, blue);
	}

	@Override
	public void glSecondaryColor3uiv(final IntBuffer v) {
		super.glSecondaryColor3uiv(v);
	}

	@Override
	public void glSecondaryColor3uiv(final int[] v, final int v_offset) {
		super.glSecondaryColor3uiv(v, v_offset);
	}

	@Override
	public void glSecondaryColor3us(final short red, final short green, final short blue) {
		super.glSecondaryColor3us(red, green, blue);
	}

	@Override
	public void glSecondaryColor3usv(final ShortBuffer v) {
		super.glSecondaryColor3usv(v);
	}

	@Override
	public void glSecondaryColor3usv(final short[] v, final int v_offset) {
		super.glSecondaryColor3usv(v, v_offset);
	}

	@Override
	public void glSecondaryColorPointer(final int size, final int type, final int stride, final Buffer pointer) {
		super.glSecondaryColorPointer(size, type, stride, pointer);
	}

	@Override
	public void glSecondaryColorPointer(final int size, final int type, final int stride,
			final long pointer_buffer_offset) {
		super.glSecondaryColorPointer(size, type, stride, pointer_buffer_offset);
	}

	@Override
	public void glWindowPos2d(final double x, final double y) {
		super.glWindowPos2d(x, y);
	}

	@Override
	public void glWindowPos2dv(final DoubleBuffer v) {
		super.glWindowPos2dv(v);
	}

	@Override
	public void glWindowPos2dv(final double[] v, final int v_offset) {
		super.glWindowPos2dv(v, v_offset);
	}

	@Override
	public void glWindowPos2f(final float x, final float y) {
		super.glWindowPos2f(x, y);
	}

	@Override
	public void glWindowPos2fv(final FloatBuffer v) {
		super.glWindowPos2fv(v);
	}

	@Override
	public void glWindowPos2fv(final float[] v, final int v_offset) {
		super.glWindowPos2fv(v, v_offset);
	}

	@Override
	public void glWindowPos2i(final int x, final int y) {
		super.glWindowPos2i(x, y);
	}

	@Override
	public void glWindowPos2iv(final IntBuffer v) {
		super.glWindowPos2iv(v);
	}

	@Override
	public void glWindowPos2iv(final int[] v, final int v_offset) {
		super.glWindowPos2iv(v, v_offset);
	}

	@Override
	public void glWindowPos2s(final short x, final short y) {
		super.glWindowPos2s(x, y);
	}

	@Override
	public void glWindowPos2sv(final ShortBuffer v) {
		super.glWindowPos2sv(v);
	}

	@Override
	public void glWindowPos2sv(final short[] v, final int v_offset) {
		super.glWindowPos2sv(v, v_offset);
	}

	@Override
	public void glWindowPos3d(final double x, final double y, final double z) {
		super.glWindowPos3d(x, y, z);
	}

	@Override
	public void glWindowPos3dv(final DoubleBuffer v) {
		super.glWindowPos3dv(v);
	}

	@Override
	public void glWindowPos3dv(final double[] v, final int v_offset) {
		super.glWindowPos3dv(v, v_offset);
	}

	@Override
	public void glWindowPos3f(final float x, final float y, final float z) {
		super.glWindowPos3f(x, y, z);
	}

	@Override
	public void glWindowPos3fv(final FloatBuffer v) {
		super.glWindowPos3fv(v);
	}

	@Override
	public void glWindowPos3fv(final float[] v, final int v_offset) {
		super.glWindowPos3fv(v, v_offset);
	}

	@Override
	public void glWindowPos3i(final int x, final int y, final int z) {
		super.glWindowPos3i(x, y, z);
	}

	@Override
	public void glWindowPos3iv(final IntBuffer v) {
		super.glWindowPos3iv(v);
	}

	@Override
	public void glWindowPos3iv(final int[] v, final int v_offset) {
		super.glWindowPos3iv(v, v_offset);
	}

	@Override
	public void glWindowPos3s(final short x, final short y, final short z) {
		super.glWindowPos3s(x, y, z);
	}

	@Override
	public void glWindowPos3sv(final ShortBuffer v) {
		super.glWindowPos3sv(v);
	}

	@Override
	public void glWindowPos3sv(final short[] v, final int v_offset) {
		super.glWindowPos3sv(v, v_offset);
	}

	@Override
	public void glBlendColor(final float red, final float green, final float blue, final float alpha) {
		super.glBlendColor(red, green, blue, alpha);
	}

	@Override
	public void glBlendEquation(final int mode) {
		super.glBlendEquation(mode);
	}

	@Override
	public void glGenQueries(final int n, final IntBuffer ids) {
		super.glGenQueries(n, ids);
	}

	@Override
	public void glGenQueries(final int n, final int[] ids, final int ids_offset) {
		super.glGenQueries(n, ids, ids_offset);
	}

	@Override
	public void glDeleteQueries(final int n, final IntBuffer ids) {
		super.glDeleteQueries(n, ids);
	}

	@Override
	public void glDeleteQueries(final int n, final int[] ids, final int ids_offset) {
		super.glDeleteQueries(n, ids, ids_offset);
	}

	@Override
	public boolean glIsQuery(final int id) {
		return super.glIsQuery(id);
	}

	@Override
	public void glBeginQuery(final int target, final int id) {
		super.glBeginQuery(target, id);
	}

	@Override
	public void glEndQuery(final int target) {
		super.glEndQuery(target);
	}

	@Override
	public void glGetQueryiv(final int target, final int pname, final IntBuffer params) {
		super.glGetQueryiv(target, pname, params);
	}

	@Override
	public void glGetQueryiv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetQueryiv(target, pname, params, params_offset);
	}

	@Override
	public void glGetQueryObjectiv(final int id, final int pname, final IntBuffer params) {
		super.glGetQueryObjectiv(id, pname, params);
	}

	@Override
	public void glGetQueryObjectiv(final int id, final int pname, final int[] params, final int params_offset) {
		super.glGetQueryObjectiv(id, pname, params, params_offset);
	}

	@Override
	public void glGetQueryObjectuiv(final int id, final int pname, final IntBuffer params) {
		super.glGetQueryObjectuiv(id, pname, params);
	}

	@Override
	public void glGetQueryObjectuiv(final int id, final int pname, final int[] params, final int params_offset) {
		super.glGetQueryObjectuiv(id, pname, params, params_offset);
	}

	@Override
	public void glBindBuffer(final int target, final int buffer) {
		super.glBindBuffer(target, buffer);
	}

	@Override
	public void glDeleteBuffers(final int n, final IntBuffer buffers) {
		super.glDeleteBuffers(n, buffers);
	}

	@Override
	public void glDeleteBuffers(final int n, final int[] buffers, final int buffers_offset) {
		super.glDeleteBuffers(n, buffers, buffers_offset);
	}

	@Override
	public void glGenBuffers(final int n, final IntBuffer buffers) {
		super.glGenBuffers(n, buffers);
	}

	@Override
	public void glGenBuffers(final int n, final int[] buffers, final int buffers_offset) {
		super.glGenBuffers(n, buffers, buffers_offset);
	}

	@Override
	public boolean glIsBuffer(final int buffer) {
		return super.glIsBuffer(buffer);
	}

	@Override
	public void glBufferSubData(final int target, final long offset, final long size, final Buffer data) {
		super.glBufferSubData(target, offset, size, data);
	}

	@Override
	public void glGetBufferSubData(final int target, final long offset, final long size, final Buffer data) {
		super.glGetBufferSubData(target, offset, size, data);
	}

	@Override
	public void glGetBufferParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetBufferParameteriv(target, pname, params);
	}

	@Override
	public void glGetBufferParameteriv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetBufferParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glBlendEquationSeparate(final int modeRGB, final int modeAlpha) {
		super.glBlendEquationSeparate(modeRGB, modeAlpha);
	}

	@Override
	public void glDrawBuffers(final int n, final IntBuffer bufs) {
		super.glDrawBuffers(n, bufs);
	}

	@Override
	public void glDrawBuffers(final int n, final int[] bufs, final int bufs_offset) {
		super.glDrawBuffers(n, bufs, bufs_offset);
	}

	@Override
	public void glStencilOpSeparate(final int face, final int sfail, final int dpfail, final int dppass) {
		super.glStencilOpSeparate(face, sfail, dpfail, dppass);
	}

	@Override
	public void glStencilFuncSeparate(final int face, final int func, final int ref, final int mask) {
		super.glStencilFuncSeparate(face, func, ref, mask);
	}

	@Override
	public void glStencilMaskSeparate(final int face, final int mask) {
		super.glStencilMaskSeparate(face, mask);
	}

	@Override
	public void glAttachShader(final int program, final int shader) {
		super.glAttachShader(program, shader);
	}

	@Override
	public void glBindAttribLocation(final int program, final int index, final String name) {
		super.glBindAttribLocation(program, index, name);
	}

	@Override
	public void glCompileShader(final int shader) {
		super.glCompileShader(shader);
	}

	@Override
	public int glCreateProgram() {
		return super.glCreateProgram();
	}

	@Override
	public int glCreateShader(final int type) {
		return super.glCreateShader(type);
	}

	@Override
	public void glDeleteProgram(final int program) {
		super.glDeleteProgram(program);
	}

	@Override
	public void glDeleteShader(final int shader) {
		super.glDeleteShader(shader);
	}

	@Override
	public void glDetachShader(final int program, final int shader) {
		super.glDetachShader(program, shader);
	}

	@Override
	public void glDisableVertexAttribArray(final int index) {
		super.glDisableVertexAttribArray(index);
	}

	@Override
	public void glEnableVertexAttribArray(final int index) {
		super.glEnableVertexAttribArray(index);
	}

	@Override
	public void glGetActiveAttrib(final int program, final int index, final int bufSize, final IntBuffer length,
			final IntBuffer size, final IntBuffer type, final ByteBuffer name) {
		super.glGetActiveAttrib(program, index, bufSize, length, size, type, name);
	}

	@Override
	public void glGetActiveAttrib(final int program, final int index, final int bufSize, final int[] length,
			final int length_offset, final int[] size, final int size_offset, final int[] type, final int type_offset,
			final byte[] name, final int name_offset) {
		super.glGetActiveAttrib(program, index, bufSize, length, length_offset, size, size_offset, type, type_offset,
				name, name_offset);
	}

	@Override
	public void glGetActiveUniform(final int program, final int index, final int bufSize, final IntBuffer length,
			final IntBuffer size, final IntBuffer type, final ByteBuffer name) {
		super.glGetActiveUniform(program, index, bufSize, length, size, type, name);
	}

	@Override
	public void glGetActiveUniform(final int program, final int index, final int bufSize, final int[] length,
			final int length_offset, final int[] size, final int size_offset, final int[] type, final int type_offset,
			final byte[] name, final int name_offset) {
		super.glGetActiveUniform(program, index, bufSize, length, length_offset, size, size_offset, type, type_offset,
				name, name_offset);
	}

	@Override
	public void glGetAttachedShaders(final int program, final int maxCount, final IntBuffer count,
			final IntBuffer shaders) {
		super.glGetAttachedShaders(program, maxCount, count, shaders);
	}

	@Override
	public void glGetAttachedShaders(final int program, final int maxCount, final int[] count, final int count_offset,
			final int[] shaders, final int shaders_offset) {
		super.glGetAttachedShaders(program, maxCount, count, count_offset, shaders, shaders_offset);
	}

	@Override
	public int glGetAttribLocation(final int program, final String name) {
		return super.glGetAttribLocation(program, name);
	}

	@Override
	public void glGetProgramiv(final int program, final int pname, final IntBuffer params) {
		super.glGetProgramiv(program, pname, params);
	}

	@Override
	public void glGetProgramiv(final int program, final int pname, final int[] params, final int params_offset) {
		super.glGetProgramiv(program, pname, params, params_offset);
	}

	@Override
	public void glGetProgramInfoLog(final int program, final int bufSize, final IntBuffer length,
			final ByteBuffer infoLog) {
		super.glGetProgramInfoLog(program, bufSize, length, infoLog);
	}

	@Override
	public void glGetProgramInfoLog(final int program, final int bufSize, final int[] length, final int length_offset,
			final byte[] infoLog, final int infoLog_offset) {
		super.glGetProgramInfoLog(program, bufSize, length, length_offset, infoLog, infoLog_offset);
	}

	@Override
	public void glGetShaderiv(final int shader, final int pname, final IntBuffer params) {
		super.glGetShaderiv(shader, pname, params);
	}

	@Override
	public void glGetShaderiv(final int shader, final int pname, final int[] params, final int params_offset) {
		super.glGetShaderiv(shader, pname, params, params_offset);
	}

	@Override
	public void glGetShaderInfoLog(final int shader, final int bufSize, final IntBuffer length,
			final ByteBuffer infoLog) {
		super.glGetShaderInfoLog(shader, bufSize, length, infoLog);
	}

	@Override
	public void glGetShaderInfoLog(final int shader, final int bufSize, final int[] length, final int length_offset,
			final byte[] infoLog, final int infoLog_offset) {
		super.glGetShaderInfoLog(shader, bufSize, length, length_offset, infoLog, infoLog_offset);
	}

	@Override
	public void glGetShaderSource(final int shader, final int bufSize, final IntBuffer length,
			final ByteBuffer source) {
		super.glGetShaderSource(shader, bufSize, length, source);
	}

	@Override
	public void glGetShaderSource(final int shader, final int bufSize, final int[] length, final int length_offset,
			final byte[] source, final int source_offset) {
		super.glGetShaderSource(shader, bufSize, length, length_offset, source, source_offset);
	}

	@Override
	public int glGetUniformLocation(final int program, final String name) {
		return super.glGetUniformLocation(program, name);
	}

	@Override
	public void glGetUniformfv(final int program, final int location, final FloatBuffer params) {
		super.glGetUniformfv(program, location, params);
	}

	@Override
	public void glGetUniformfv(final int program, final int location, final float[] params, final int params_offset) {
		super.glGetUniformfv(program, location, params, params_offset);
	}

	@Override
	public void glGetUniformiv(final int program, final int location, final IntBuffer params) {
		super.glGetUniformiv(program, location, params);
	}

	@Override
	public void glGetUniformiv(final int program, final int location, final int[] params, final int params_offset) {
		super.glGetUniformiv(program, location, params, params_offset);
	}

	@Override
	public void glGetVertexAttribdv(final int index, final int pname, final DoubleBuffer params) {
		super.glGetVertexAttribdv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribdv(final int index, final int pname, final double[] params, final int params_offset) {
		super.glGetVertexAttribdv(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribfv(final int index, final int pname, final FloatBuffer params) {
		super.glGetVertexAttribfv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribfv(final int index, final int pname, final float[] params, final int params_offset) {
		super.glGetVertexAttribfv(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribiv(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribiv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribiv(final int index, final int pname, final int[] params, final int params_offset) {
		super.glGetVertexAttribiv(index, pname, params, params_offset);
	}

	@Override
	public boolean glIsProgram(final int program) {
		return super.glIsProgram(program);
	}

	@Override
	public boolean glIsShader(final int shader) {
		return super.glIsShader(shader);
	}

	@Override
	public void glLinkProgram(final int program) {
		super.glLinkProgram(program);
	}

	@Override
	public void glShaderSource(final int shader, final int count, final String[] string, final IntBuffer length) {
		super.glShaderSource(shader, count, string, length);
	}

	@Override
	public void glShaderSource(final int shader, final int count, final String[] string, final int[] length,
			final int length_offset) {
		super.glShaderSource(shader, count, string, length, length_offset);
	}

	@Override
	public void glUseProgram(final int program) {
		super.glUseProgram(program);
	}

	@Override
	public void glUniform1f(final int location, final float v0) {
		super.glUniform1f(location, v0);
	}

	@Override
	public void glUniform2f(final int location, final float v0, final float v1) {
		super.glUniform2f(location, v0, v1);
	}

	@Override
	public void glUniform3f(final int location, final float v0, final float v1, final float v2) {
		super.glUniform3f(location, v0, v1, v2);
	}

	@Override
	public void glUniform4f(final int location, final float v0, final float v1, final float v2, final float v3) {
		super.glUniform4f(location, v0, v1, v2, v3);
	}

	@Override
	public void glUniform1i(final int location, final int v0) {
		super.glUniform1i(location, v0);
	}

	@Override
	public void glUniform2i(final int location, final int v0, final int v1) {
		super.glUniform2i(location, v0, v1);
	}

	@Override
	public void glUniform3i(final int location, final int v0, final int v1, final int v2) {
		super.glUniform3i(location, v0, v1, v2);
	}

	@Override
	public void glUniform4i(final int location, final int v0, final int v1, final int v2, final int v3) {
		super.glUniform4i(location, v0, v1, v2, v3);
	}

	@Override
	public void glUniform1fv(final int location, final int count, final FloatBuffer value) {
		super.glUniform1fv(location, count, value);
	}

	@Override
	public void glUniform1fv(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform1fv(location, count, value, value_offset);
	}

	@Override
	public void glUniform2fv(final int location, final int count, final FloatBuffer value) {
		super.glUniform2fv(location, count, value);
	}

	@Override
	public void glUniform2fv(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform2fv(location, count, value, value_offset);
	}

	@Override
	public void glUniform3fv(final int location, final int count, final FloatBuffer value) {
		super.glUniform3fv(location, count, value);
	}

	@Override
	public void glUniform3fv(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform3fv(location, count, value, value_offset);
	}

	@Override
	public void glUniform4fv(final int location, final int count, final FloatBuffer value) {
		super.glUniform4fv(location, count, value);
	}

	@Override
	public void glUniform4fv(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform4fv(location, count, value, value_offset);
	}

	@Override
	public void glUniform1iv(final int location, final int count, final IntBuffer value) {
		super.glUniform1iv(location, count, value);
	}

	@Override
	public void glUniform1iv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform1iv(location, count, value, value_offset);
	}

	@Override
	public void glUniform2iv(final int location, final int count, final IntBuffer value) {
		super.glUniform2iv(location, count, value);
	}

	@Override
	public void glUniform2iv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform2iv(location, count, value, value_offset);
	}

	@Override
	public void glUniform3iv(final int location, final int count, final IntBuffer value) {
		super.glUniform3iv(location, count, value);
	}

	@Override
	public void glUniform3iv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform3iv(location, count, value, value_offset);
	}

	@Override
	public void glUniform4iv(final int location, final int count, final IntBuffer value) {
		super.glUniform4iv(location, count, value);
	}

	@Override
	public void glUniform4iv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform4iv(location, count, value, value_offset);
	}

	@Override
	public void glUniformMatrix2fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix2fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix3fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix3fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix4fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix4fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glValidateProgram(final int program) {
		super.glValidateProgram(program);
	}

	@Override
	public void glVertexAttrib1d(final int index, final double x) {
		super.glVertexAttrib1d(index, x);
	}

	@Override
	public void glVertexAttrib1dv(final int index, final DoubleBuffer v) {
		super.glVertexAttrib1dv(index, v);
	}

	@Override
	public void glVertexAttrib1dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib1dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib1f(final int index, final float x) {
		super.glVertexAttrib1f(index, x);
	}

	@Override
	public void glVertexAttrib1fv(final int index, final FloatBuffer v) {
		super.glVertexAttrib1fv(index, v);
	}

	@Override
	public void glVertexAttrib1fv(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib1fv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib1s(final int index, final short x) {
		super.glVertexAttrib1s(index, x);
	}

	@Override
	public void glVertexAttrib1sv(final int index, final ShortBuffer v) {
		super.glVertexAttrib1sv(index, v);
	}

	@Override
	public void glVertexAttrib1sv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib1sv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2d(final int index, final double x, final double y) {
		super.glVertexAttrib2d(index, x, y);
	}

	@Override
	public void glVertexAttrib2dv(final int index, final DoubleBuffer v) {
		super.glVertexAttrib2dv(index, v);
	}

	@Override
	public void glVertexAttrib2dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib2dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2f(final int index, final float x, final float y) {
		super.glVertexAttrib2f(index, x, y);
	}

	@Override
	public void glVertexAttrib2fv(final int index, final FloatBuffer v) {
		super.glVertexAttrib2fv(index, v);
	}

	@Override
	public void glVertexAttrib2fv(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib2fv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2s(final int index, final short x, final short y) {
		super.glVertexAttrib2s(index, x, y);
	}

	@Override
	public void glVertexAttrib2sv(final int index, final ShortBuffer v) {
		super.glVertexAttrib2sv(index, v);
	}

	@Override
	public void glVertexAttrib2sv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib2sv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3d(final int index, final double x, final double y, final double z) {
		super.glVertexAttrib3d(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3dv(final int index, final DoubleBuffer v) {
		super.glVertexAttrib3dv(index, v);
	}

	@Override
	public void glVertexAttrib3dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib3dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3f(final int index, final float x, final float y, final float z) {
		super.glVertexAttrib3f(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3fv(final int index, final FloatBuffer v) {
		super.glVertexAttrib3fv(index, v);
	}

	@Override
	public void glVertexAttrib3fv(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib3fv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3s(final int index, final short x, final short y, final short z) {
		super.glVertexAttrib3s(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3sv(final int index, final ShortBuffer v) {
		super.glVertexAttrib3sv(index, v);
	}

	@Override
	public void glVertexAttrib3sv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib3sv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Nbv(final int index, final ByteBuffer v) {
		super.glVertexAttrib4Nbv(index, v);
	}

	@Override
	public void glVertexAttrib4Nbv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4Nbv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Niv(final int index, final IntBuffer v) {
		super.glVertexAttrib4Niv(index, v);
	}

	@Override
	public void glVertexAttrib4Niv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4Niv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Nsv(final int index, final ShortBuffer v) {
		super.glVertexAttrib4Nsv(index, v);
	}

	@Override
	public void glVertexAttrib4Nsv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4Nsv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Nub(final int index, final byte x, final byte y, final byte z, final byte w) {
		super.glVertexAttrib4Nub(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4Nubv(final int index, final ByteBuffer v) {
		super.glVertexAttrib4Nubv(index, v);
	}

	@Override
	public void glVertexAttrib4Nubv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4Nubv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Nuiv(final int index, final IntBuffer v) {
		super.glVertexAttrib4Nuiv(index, v);
	}

	@Override
	public void glVertexAttrib4Nuiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4Nuiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4Nusv(final int index, final ShortBuffer v) {
		super.glVertexAttrib4Nusv(index, v);
	}

	@Override
	public void glVertexAttrib4Nusv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4Nusv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4bv(final int index, final ByteBuffer v) {
		super.glVertexAttrib4bv(index, v);
	}

	@Override
	public void glVertexAttrib4bv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4bv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4d(final int index, final double x, final double y, final double z, final double w) {
		super.glVertexAttrib4d(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4dv(final int index, final DoubleBuffer v) {
		super.glVertexAttrib4dv(index, v);
	}

	@Override
	public void glVertexAttrib4dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib4dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4f(final int index, final float x, final float y, final float z, final float w) {
		super.glVertexAttrib4f(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4fv(final int index, final FloatBuffer v) {
		super.glVertexAttrib4fv(index, v);
	}

	@Override
	public void glVertexAttrib4fv(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib4fv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4iv(final int index, final IntBuffer v) {
		super.glVertexAttrib4iv(index, v);
	}

	@Override
	public void glVertexAttrib4iv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4iv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4s(final int index, final short x, final short y, final short z, final short w) {
		super.glVertexAttrib4s(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4sv(final int index, final ShortBuffer v) {
		super.glVertexAttrib4sv(index, v);
	}

	@Override
	public void glVertexAttrib4sv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4sv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4ubv(final int index, final ByteBuffer v) {
		super.glVertexAttrib4ubv(index, v);
	}

	@Override
	public void glVertexAttrib4ubv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4ubv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4uiv(final int index, final IntBuffer v) {
		super.glVertexAttrib4uiv(index, v);
	}

	@Override
	public void glVertexAttrib4uiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4uiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4usv(final int index, final ShortBuffer v) {
		super.glVertexAttrib4usv(index, v);
	}

	@Override
	public void glVertexAttrib4usv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4usv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribPointer(final int index, final int size, final int type, final boolean normalized,
			final int stride, final Buffer pointer) {
		super.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	@Override
	public void glVertexAttribPointer(final int index, final int size, final int type, final boolean normalized,
			final int stride, final long pointer_buffer_offset) {
		super.glVertexAttribPointer(index, size, type, normalized, stride, pointer_buffer_offset);
	}

	@Override
	public void glUniformMatrix2x3fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix2x3fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2x3fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix2x3fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3x2fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix3x2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x2fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix3x2fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix2x4fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix2x4fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2x4fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix2x4fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4x2fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix4x2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4x2fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix4x2fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3x4fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix3x4fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x4fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix3x4fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4x3fv(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix4x3fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4x3fv(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix4x3fv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glColorMaski(final int index, final boolean r, final boolean g, final boolean b, final boolean a) {
		super.glColorMaski(index, r, g, b, a);
	}

	@Override
	public void glGetBooleani_v(final int target, final int index, final ByteBuffer data) {
		super.glGetBooleani_v(target, index, data);
	}

	@Override
	public void glGetBooleani_v(final int target, final int index, final byte[] data, final int data_offset) {
		super.glGetBooleani_v(target, index, data, data_offset);
	}

	@Override
	public void glGetIntegeri_v(final int target, final int index, final IntBuffer data) {
		super.glGetIntegeri_v(target, index, data);
	}

	@Override
	public void glGetIntegeri_v(final int target, final int index, final int[] data, final int data_offset) {
		super.glGetIntegeri_v(target, index, data, data_offset);
	}

	@Override
	public void glEnablei(final int target, final int index) {
		super.glEnablei(target, index);
	}

	@Override
	public void glDisablei(final int target, final int index) {
		super.glDisablei(target, index);
	}

	@Override
	public boolean glIsEnabledi(final int target, final int index) {
		return super.glIsEnabledi(target, index);
	}

	@Override
	public void glBeginTransformFeedback(final int primitiveMode) {
		super.glBeginTransformFeedback(primitiveMode);
	}

	@Override
	public void glEndTransformFeedback() {
		super.glEndTransformFeedback();
	}

	@Override
	public void glBindBufferRange(final int target, final int index, final int buffer, final long offset,
			final long size) {
		super.glBindBufferRange(target, index, buffer, offset, size);
	}

	@Override
	public void glBindBufferBase(final int target, final int index, final int buffer) {
		super.glBindBufferBase(target, index, buffer);
	}

	@Override
	public void glTransformFeedbackVaryings(final int program, final int count, final String[] varyings,
			final int bufferMode) {
		super.glTransformFeedbackVaryings(program, count, varyings, bufferMode);
	}

	@Override
	public void glGetTransformFeedbackVarying(final int program, final int index, final int bufSize,
			final IntBuffer length, final IntBuffer size, final IntBuffer type, final ByteBuffer name) {
		super.glGetTransformFeedbackVarying(program, index, bufSize, length, size, type, name);
	}

	@Override
	public void glGetTransformFeedbackVarying(final int program, final int index, final int bufSize, final int[] length,
			final int length_offset, final int[] size, final int size_offset, final int[] type, final int type_offset,
			final byte[] name, final int name_offset) {
		super.glGetTransformFeedbackVarying(program, index, bufSize, length, length_offset, size, size_offset, type,
				type_offset, name, name_offset);
	}

	@Override
	public void glClampColor(final int target, final int clamp) {
		super.glClampColor(target, clamp);
	}

	@Override
	public void glBeginConditionalRender(final int id, final int mode) {
		super.glBeginConditionalRender(id, mode);
	}

	@Override
	public void glEndConditionalRender() {
		super.glEndConditionalRender();
	}

	@Override
	public void glVertexAttribIPointer(final int index, final int size, final int type, final int stride,
			final Buffer pointer) {
		super.glVertexAttribIPointer(index, size, type, stride, pointer);
	}

	@Override
	public void glVertexAttribIPointer(final int index, final int size, final int type, final int stride,
			final long pointer_buffer_offset) {
		super.glVertexAttribIPointer(index, size, type, stride, pointer_buffer_offset);
	}

	@Override
	public void glGetVertexAttribIiv(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribIiv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribIiv(final int index, final int pname, final int[] params, final int params_offset) {
		super.glGetVertexAttribIiv(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribIuiv(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribIuiv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribIuiv(final int index, final int pname, final int[] params, final int params_offset) {
		super.glGetVertexAttribIuiv(index, pname, params, params_offset);
	}

	@Override
	public void glVertexAttribI1i(final int index, final int x) {
		super.glVertexAttribI1i(index, x);
	}

	@Override
	public void glVertexAttribI2i(final int index, final int x, final int y) {
		super.glVertexAttribI2i(index, x, y);
	}

	@Override
	public void glVertexAttribI3i(final int index, final int x, final int y, final int z) {
		super.glVertexAttribI3i(index, x, y, z);
	}

	@Override
	public void glVertexAttribI4i(final int index, final int x, final int y, final int z, final int w) {
		super.glVertexAttribI4i(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribI1ui(final int index, final int x) {
		super.glVertexAttribI1ui(index, x);
	}

	@Override
	public void glVertexAttribI2ui(final int index, final int x, final int y) {
		super.glVertexAttribI2ui(index, x, y);
	}

	@Override
	public void glVertexAttribI3ui(final int index, final int x, final int y, final int z) {
		super.glVertexAttribI3ui(index, x, y, z);
	}

	@Override
	public void glVertexAttribI4ui(final int index, final int x, final int y, final int z, final int w) {
		super.glVertexAttribI4ui(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribI1iv(final int index, final IntBuffer v) {
		super.glVertexAttribI1iv(index, v);
	}

	@Override
	public void glVertexAttribI1iv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI1iv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI2iv(final int index, final IntBuffer v) {
		super.glVertexAttribI2iv(index, v);
	}

	@Override
	public void glVertexAttribI2iv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI2iv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI3iv(final int index, final IntBuffer v) {
		super.glVertexAttribI3iv(index, v);
	}

	@Override
	public void glVertexAttribI3iv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI3iv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4iv(final int index, final IntBuffer v) {
		super.glVertexAttribI4iv(index, v);
	}

	@Override
	public void glVertexAttribI4iv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI4iv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI1uiv(final int index, final IntBuffer v) {
		super.glVertexAttribI1uiv(index, v);
	}

	@Override
	public void glVertexAttribI1uiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI1uiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI2uiv(final int index, final IntBuffer v) {
		super.glVertexAttribI2uiv(index, v);
	}

	@Override
	public void glVertexAttribI2uiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI2uiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI3uiv(final int index, final IntBuffer v) {
		super.glVertexAttribI3uiv(index, v);
	}

	@Override
	public void glVertexAttribI3uiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI3uiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4uiv(final int index, final IntBuffer v) {
		super.glVertexAttribI4uiv(index, v);
	}

	@Override
	public void glVertexAttribI4uiv(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI4uiv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4bv(final int index, final ByteBuffer v) {
		super.glVertexAttribI4bv(index, v);
	}

	@Override
	public void glVertexAttribI4bv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttribI4bv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4sv(final int index, final ShortBuffer v) {
		super.glVertexAttribI4sv(index, v);
	}

	@Override
	public void glVertexAttribI4sv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttribI4sv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4ubv(final int index, final ByteBuffer v) {
		super.glVertexAttribI4ubv(index, v);
	}

	@Override
	public void glVertexAttribI4ubv(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttribI4ubv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4usv(final int index, final ShortBuffer v) {
		super.glVertexAttribI4usv(index, v);
	}

	@Override
	public void glVertexAttribI4usv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttribI4usv(index, v, v_offset);
	}

	@Override
	public void glGetUniformuiv(final int program, final int location, final IntBuffer params) {
		super.glGetUniformuiv(program, location, params);
	}

	@Override
	public void glGetUniformuiv(final int program, final int location, final int[] params, final int params_offset) {
		super.glGetUniformuiv(program, location, params, params_offset);
	}

	@Override
	public void glBindFragDataLocation(final int program, final int color, final String name) {
		super.glBindFragDataLocation(program, color, name);
	}

	@Override
	public int glGetFragDataLocation(final int program, final String name) {
		return super.glGetFragDataLocation(program, name);
	}

	@Override
	public void glUniform1ui(final int location, final int v0) {
		super.glUniform1ui(location, v0);
	}

	@Override
	public void glUniform2ui(final int location, final int v0, final int v1) {
		super.glUniform2ui(location, v0, v1);
	}

	@Override
	public void glUniform3ui(final int location, final int v0, final int v1, final int v2) {
		super.glUniform3ui(location, v0, v1, v2);
	}

	@Override
	public void glUniform4ui(final int location, final int v0, final int v1, final int v2, final int v3) {
		super.glUniform4ui(location, v0, v1, v2, v3);
	}

	@Override
	public void glUniform1uiv(final int location, final int count, final IntBuffer value) {
		super.glUniform1uiv(location, count, value);
	}

	@Override
	public void glUniform1uiv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform1uiv(location, count, value, value_offset);
	}

	@Override
	public void glUniform2uiv(final int location, final int count, final IntBuffer value) {
		super.glUniform2uiv(location, count, value);
	}

	@Override
	public void glUniform2uiv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform2uiv(location, count, value, value_offset);
	}

	@Override
	public void glUniform3uiv(final int location, final int count, final IntBuffer value) {
		super.glUniform3uiv(location, count, value);
	}

	@Override
	public void glUniform3uiv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform3uiv(location, count, value, value_offset);
	}

	@Override
	public void glUniform4uiv(final int location, final int count, final IntBuffer value) {
		super.glUniform4uiv(location, count, value);
	}

	@Override
	public void glUniform4uiv(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform4uiv(location, count, value, value_offset);
	}

	@Override
	public void glTexParameterIiv(final int target, final int pname, final IntBuffer params) {
		super.glTexParameterIiv(target, pname, params);
	}

	@Override
	public void glTexParameterIiv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glTexParameterIiv(target, pname, params, params_offset);
	}

	@Override
	public void glTexParameterIuiv(final int target, final int pname, final IntBuffer params) {
		super.glTexParameterIuiv(target, pname, params);
	}

	@Override
	public void glTexParameterIuiv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glTexParameterIuiv(target, pname, params, params_offset);
	}

	@Override
	public void glGetTexParameterIiv(final int target, final int pname, final IntBuffer params) {
		super.glGetTexParameterIiv(target, pname, params);
	}

	@Override
	public void glGetTexParameterIiv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetTexParameterIiv(target, pname, params, params_offset);
	}

	@Override
	public void glGetTexParameterIuiv(final int target, final int pname, final IntBuffer params) {
		super.glGetTexParameterIuiv(target, pname, params);
	}

	@Override
	public void glGetTexParameterIuiv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetTexParameterIuiv(target, pname, params, params_offset);
	}

	@Override
	public void glClearBufferiv(final int buffer, final int drawbuffer, final IntBuffer value) {
		super.glClearBufferiv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferiv(final int buffer, final int drawbuffer, final int[] value, final int value_offset) {
		super.glClearBufferiv(buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearBufferuiv(final int buffer, final int drawbuffer, final IntBuffer value) {
		super.glClearBufferuiv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferuiv(final int buffer, final int drawbuffer, final int[] value, final int value_offset) {
		super.glClearBufferuiv(buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearBufferfv(final int buffer, final int drawbuffer, final FloatBuffer value) {
		super.glClearBufferfv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferfv(final int buffer, final int drawbuffer, final float[] value, final int value_offset) {
		super.glClearBufferfv(buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearBufferfi(final int buffer, final int drawbuffer, final float depth, final int stencil) {
		super.glClearBufferfi(buffer, drawbuffer, depth, stencil);
	}

	@Override
	public String glGetStringi(final int name, final int index) {
		return super.glGetStringi(name, index);
	}

	@Override
	public boolean glIsRenderbuffer(final int renderbuffer) {
		return super.glIsRenderbuffer(renderbuffer);
	}

	@Override
	public void glBindRenderbuffer(final int target, final int renderbuffer) {
		super.glBindRenderbuffer(target, renderbuffer);
	}

	@Override
	public void glDeleteRenderbuffers(final int n, final IntBuffer renderbuffers) {
		super.glDeleteRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glDeleteRenderbuffers(final int n, final int[] renderbuffers, final int renderbuffers_offset) {
		super.glDeleteRenderbuffers(n, renderbuffers, renderbuffers_offset);
	}

	@Override
	public void glGenRenderbuffers(final int n, final IntBuffer renderbuffers) {
		super.glGenRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glGenRenderbuffers(final int n, final int[] renderbuffers, final int renderbuffers_offset) {
		super.glGenRenderbuffers(n, renderbuffers, renderbuffers_offset);
	}

	@Override
	public void glRenderbufferStorage(final int target, final int internalformat, final int width, final int height) {
		super.glRenderbufferStorage(target, internalformat, width, height);
	}

	@Override
	public void glGetRenderbufferParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetRenderbufferParameteriv(target, pname, params);
	}

	@Override
	public void glGetRenderbufferParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetRenderbufferParameteriv(target, pname, params, params_offset);
	}

	@Override
	public boolean glIsFramebuffer(final int framebuffer) {
		return super.glIsFramebuffer(framebuffer);
	}

	@Override
	public void glBindFramebuffer(final int target, final int framebuffer) {
		super.glBindFramebuffer(target, framebuffer);
	}

	@Override
	public void glDeleteFramebuffers(final int n, final IntBuffer framebuffers) {
		super.glDeleteFramebuffers(n, framebuffers);
	}

	@Override
	public void glDeleteFramebuffers(final int n, final int[] framebuffers, final int framebuffers_offset) {
		super.glDeleteFramebuffers(n, framebuffers, framebuffers_offset);
	}

	@Override
	public void glGenFramebuffers(final int n, final IntBuffer framebuffers) {
		super.glGenFramebuffers(n, framebuffers);
	}

	@Override
	public void glGenFramebuffers(final int n, final int[] framebuffers, final int framebuffers_offset) {
		super.glGenFramebuffers(n, framebuffers, framebuffers_offset);
	}

	@Override
	public int glCheckFramebufferStatus(final int target) {
		return super.glCheckFramebufferStatus(target);
	}

	@Override
	public void glFramebufferTexture1D(final int target, final int attachment, final int textarget, final int texture,
			final int level) {
		super.glFramebufferTexture1D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glFramebufferTexture2D(final int target, final int attachment, final int textarget, final int texture,
			final int level) {
		super.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glFramebufferTexture3D(final int target, final int attachment, final int textarget, final int texture,
			final int level, final int zoffset) {
		super.glFramebufferTexture3D(target, attachment, textarget, texture, level, zoffset);
	}

	@Override
	public void glFramebufferRenderbuffer(final int target, final int attachment, final int renderbuffertarget,
			final int renderbuffer) {
		super.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(final int target, final int attachment, final int pname,
			final IntBuffer params) {
		super.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
	}

	@Override
	public void glGetFramebufferAttachmentParameteriv(final int target, final int attachment, final int pname,
			final int[] params, final int params_offset) {
		super.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params, params_offset);
	}

	@Override
	public void glGenerateMipmap(final int target) {
		super.glGenerateMipmap(target);
	}

	@Override
	public void glBlitFramebuffer(final int srcX0, final int srcY0, final int srcX1, final int srcY1, final int dstX0,
			final int dstY0, final int dstX1, final int dstY1, final int mask, final int filter) {
		super.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	@Override
	public void glRenderbufferStorageMultisample(final int target, final int samples, final int internalformat,
			final int width, final int height) {
		super.glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
	}

	@Override
	public void glFramebufferTextureLayer(final int target, final int attachment, final int texture, final int level,
			final int layer) {
		super.glFramebufferTextureLayer(target, attachment, texture, level, layer);
	}

	@Override
	public void glFlushMappedBufferRange(final int target, final long offset, final long length) {
		super.glFlushMappedBufferRange(target, offset, length);
	}

	@Override
	public void glBindVertexArray(final int array) {
		super.glBindVertexArray(array);
	}

	@Override
	public void glDeleteVertexArrays(final int n, final IntBuffer arrays) {
		super.glDeleteVertexArrays(n, arrays);
	}

	@Override
	public void glDeleteVertexArrays(final int n, final int[] arrays, final int arrays_offset) {
		super.glDeleteVertexArrays(n, arrays, arrays_offset);
	}

	@Override
	public void glGenVertexArrays(final int n, final IntBuffer arrays) {
		super.glGenVertexArrays(n, arrays);
	}

	@Override
	public void glGenVertexArrays(final int n, final int[] arrays, final int arrays_offset) {
		super.glGenVertexArrays(n, arrays, arrays_offset);
	}

	@Override
	public boolean glIsVertexArray(final int array) {
		return super.glIsVertexArray(array);
	}

	@Override
	public void glDrawArraysInstanced(final int mode, final int first, final int count, final int instancecount) {
		super.glDrawArraysInstanced(mode, first, count, instancecount);
	}

	@Override
	public void glDrawElementsInstanced(final int mode, final int count, final int type, final Buffer indices,
			final int instancecount) {
		super.glDrawElementsInstanced(mode, count, type, indices, instancecount);
	}

	@Override
	public void glDrawElementsInstanced(final int mode, final int count, final int type,
			final long indices_buffer_offset, final int instancecount) {
		super.glDrawElementsInstanced(mode, count, type, indices_buffer_offset, instancecount);
	}

	@Override
	public void glTexBuffer(final int target, final int internalformat, final int buffer) {
		super.glTexBuffer(target, internalformat, buffer);
	}

	@Override
	public void glPrimitiveRestartIndex(final int index) {
		super.glPrimitiveRestartIndex(index);
	}

	@Override
	public void glCopyBufferSubData(final int readTarget, final int writeTarget, final long readOffset,
			final long writeOffset, final long size) {
		super.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
	}

	@Override
	public void glGetUniformIndices(final int program, final int uniformCount, final String[] uniformNames,
			final IntBuffer uniformIndices) {
		super.glGetUniformIndices(program, uniformCount, uniformNames, uniformIndices);
	}

	@Override
	public void glGetUniformIndices(final int program, final int uniformCount, final String[] uniformNames,
			final int[] uniformIndices, final int uniformIndices_offset) {
		super.glGetUniformIndices(program, uniformCount, uniformNames, uniformIndices, uniformIndices_offset);
	}

	@Override
	public void glGetActiveUniformsiv(final int program, final int uniformCount, final IntBuffer uniformIndices,
			final int pname, final IntBuffer params) {
		super.glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
	}

	@Override
	public void glGetActiveUniformsiv(final int program, final int uniformCount, final int[] uniformIndices,
			final int uniformIndices_offset, final int pname, final int[] params, final int params_offset) {
		super.glGetActiveUniformsiv(program, uniformCount, uniformIndices, uniformIndices_offset, pname, params,
				params_offset);
	}

	@Override
	public void glGetActiveUniformName(final int program, final int uniformIndex, final int bufSize,
			final IntBuffer length, final ByteBuffer uniformName) {
		super.glGetActiveUniformName(program, uniformIndex, bufSize, length, uniformName);
	}

	@Override
	public void glGetActiveUniformName(final int program, final int uniformIndex, final int bufSize, final int[] length,
			final int length_offset, final byte[] uniformName, final int uniformName_offset) {
		super.glGetActiveUniformName(program, uniformIndex, bufSize, length, length_offset, uniformName,
				uniformName_offset);
	}

	@Override
	public int glGetUniformBlockIndex(final int program, final String uniformBlockName) {
		return super.glGetUniformBlockIndex(program, uniformBlockName);
	}

	@Override
	public void glGetActiveUniformBlockiv(final int program, final int uniformBlockIndex, final int pname,
			final IntBuffer params) {
		super.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
	}

	@Override
	public void glGetActiveUniformBlockiv(final int program, final int uniformBlockIndex, final int pname,
			final int[] params, final int params_offset) {
		super.glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params, params_offset);
	}

	@Override
	public void glGetActiveUniformBlockName(final int program, final int uniformBlockIndex, final int bufSize,
			final IntBuffer length, final ByteBuffer uniformBlockName) {
		super.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, uniformBlockName);
	}

	@Override
	public void glGetActiveUniformBlockName(final int program, final int uniformBlockIndex, final int bufSize,
			final int[] length, final int length_offset, final byte[] uniformBlockName,
			final int uniformBlockName_offset) {
		super.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, length, length_offset, uniformBlockName,
				uniformBlockName_offset);
	}

	@Override
	public void glUniformBlockBinding(final int program, final int uniformBlockIndex, final int uniformBlockBinding) {
		super.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
	}

	@Override
	public void glDrawElementsBaseVertex(final int mode, final int count, final int type, final Buffer indices,
			final int basevertex) {
		super.glDrawElementsBaseVertex(mode, count, type, indices, basevertex);
	}

	@Override
	public void glDrawElementsBaseVertex(final int mode, final int count, final int type,
			final long indices_buffer_offset, final int basevertex) {
		super.glDrawElementsBaseVertex(mode, count, type, indices_buffer_offset, basevertex);
	}

	@Override
	public void glDrawRangeElementsBaseVertex(final int mode, final int start, final int end, final int count,
			final int type, final Buffer indices, final int basevertex) {
		super.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices, basevertex);
	}

	@Override
	public void glDrawRangeElementsBaseVertex(final int mode, final int start, final int end, final int count,
			final int type, final long indices_buffer_offset, final int basevertex) {
		super.glDrawRangeElementsBaseVertex(mode, start, end, count, type, indices_buffer_offset, basevertex);
	}

	@Override
	public void glDrawElementsInstancedBaseVertex(final int mode, final int count, final int type, final Buffer indices,
			final int instancecount, final int basevertex) {
		super.glDrawElementsInstancedBaseVertex(mode, count, type, indices, instancecount, basevertex);
	}

	@Override
	public void glDrawElementsInstancedBaseVertex(final int mode, final int count, final int type,
			final long indices_buffer_offset, final int instancecount, final int basevertex) {
		super.glDrawElementsInstancedBaseVertex(mode, count, type, indices_buffer_offset, instancecount, basevertex);
	}

	@Override
	public void glMultiDrawElementsBaseVertex(final int mode, final IntBuffer count, final int type,
			final PointerBuffer indices, final int drawcount, final IntBuffer basevertex) {
		super.glMultiDrawElementsBaseVertex(mode, count, type, indices, drawcount, basevertex);
	}

	@Override
	public void glProvokingVertex(final int mode) {
		super.glProvokingVertex(mode);
	}

	@Override
	public long glFenceSync(final int condition, final int flags) {
		return super.glFenceSync(condition, flags);
	}

	@Override
	public boolean glIsSync(final long sync) {
		return super.glIsSync(sync);
	}

	@Override
	public void glDeleteSync(final long sync) {
		super.glDeleteSync(sync);
	}

	@Override
	public int glClientWaitSync(final long sync, final int flags, final long timeout) {
		return super.glClientWaitSync(sync, flags, timeout);
	}

	@Override
	public void glWaitSync(final long sync, final int flags, final long timeout) {
		super.glWaitSync(sync, flags, timeout);
	}

	@Override
	public void glGetInteger64v(final int pname, final LongBuffer data) {
		super.glGetInteger64v(pname, data);
	}

	@Override
	public void glGetInteger64v(final int pname, final long[] data, final int data_offset) {
		super.glGetInteger64v(pname, data, data_offset);
	}

	@Override
	public void glGetSynciv(final long sync, final int pname, final int bufSize, final IntBuffer length,
			final IntBuffer values) {
		super.glGetSynciv(sync, pname, bufSize, length, values);
	}

	@Override
	public void glGetSynciv(final long sync, final int pname, final int bufSize, final int[] length,
			final int length_offset, final int[] values, final int values_offset) {
		super.glGetSynciv(sync, pname, bufSize, length, length_offset, values, values_offset);
	}

	@Override
	public void glGetInteger64i_v(final int target, final int index, final LongBuffer data) {
		super.glGetInteger64i_v(target, index, data);
	}

	@Override
	public void glGetInteger64i_v(final int target, final int index, final long[] data, final int data_offset) {
		super.glGetInteger64i_v(target, index, data, data_offset);
	}

	@Override
	public void glGetBufferParameteri64v(final int target, final int pname, final LongBuffer params) {
		super.glGetBufferParameteri64v(target, pname, params);
	}

	@Override
	public void glGetBufferParameteri64v(final int target, final int pname, final long[] params,
			final int params_offset) {
		super.glGetBufferParameteri64v(target, pname, params, params_offset);
	}

	@Override
	public void glFramebufferTexture(final int target, final int attachment, final int texture, final int level) {
		super.glFramebufferTexture(target, attachment, texture, level);
	}

	@Override
	public void glTexImage2DMultisample(final int target, final int samples, final int internalformat, final int width,
			final int height, final boolean fixedsamplelocations) {
		super.glTexImage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
	}

	@Override
	public void glTexImage3DMultisample(final int target, final int samples, final int internalformat, final int width,
			final int height, final int depth, final boolean fixedsamplelocations) {
		super.glTexImage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
	}

	@Override
	public void glGetMultisamplefv(final int pname, final int index, final FloatBuffer val) {
		super.glGetMultisamplefv(pname, index, val);
	}

	@Override
	public void glGetMultisamplefv(final int pname, final int index, final float[] val, final int val_offset) {
		super.glGetMultisamplefv(pname, index, val, val_offset);
	}

	@Override
	public void glSampleMaski(final int maskNumber, final int mask) {
		super.glSampleMaski(maskNumber, mask);
	}

	@Override
	public void glBindFragDataLocationIndexed(final int program, final int colorNumber, final int index,
			final String name) {
		super.glBindFragDataLocationIndexed(program, colorNumber, index, name);
	}

	@Override
	public int glGetFragDataIndex(final int program, final String name) {
		return super.glGetFragDataIndex(program, name);
	}

	@Override
	public void glGenSamplers(final int count, final IntBuffer samplers) {
		super.glGenSamplers(count, samplers);
	}

	@Override
	public void glGenSamplers(final int count, final int[] samplers, final int samplers_offset) {
		super.glGenSamplers(count, samplers, samplers_offset);
	}

	@Override
	public void glDeleteSamplers(final int count, final IntBuffer samplers) {
		super.glDeleteSamplers(count, samplers);
	}

	@Override
	public void glDeleteSamplers(final int count, final int[] samplers, final int samplers_offset) {
		super.glDeleteSamplers(count, samplers, samplers_offset);
	}

	@Override
	public boolean glIsSampler(final int sampler) {
		return super.glIsSampler(sampler);
	}

	@Override
	public void glBindSampler(final int unit, final int sampler) {
		super.glBindSampler(unit, sampler);
	}

	@Override
	public void glSamplerParameteri(final int sampler, final int pname, final int param) {
		super.glSamplerParameteri(sampler, pname, param);
	}

	@Override
	public void glSamplerParameteriv(final int sampler, final int pname, final IntBuffer param) {
		super.glSamplerParameteriv(sampler, pname, param);
	}

	@Override
	public void glSamplerParameteriv(final int sampler, final int pname, final int[] param, final int param_offset) {
		super.glSamplerParameteriv(sampler, pname, param, param_offset);
	}

	@Override
	public void glSamplerParameterf(final int sampler, final int pname, final float param) {
		super.glSamplerParameterf(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterfv(final int sampler, final int pname, final FloatBuffer param) {
		super.glSamplerParameterfv(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterfv(final int sampler, final int pname, final float[] param, final int param_offset) {
		super.glSamplerParameterfv(sampler, pname, param, param_offset);
	}

	@Override
	public void glSamplerParameterIiv(final int sampler, final int pname, final IntBuffer param) {
		super.glSamplerParameterIiv(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterIiv(final int sampler, final int pname, final int[] param, final int param_offset) {
		super.glSamplerParameterIiv(sampler, pname, param, param_offset);
	}

	@Override
	public void glSamplerParameterIuiv(final int sampler, final int pname, final IntBuffer param) {
		super.glSamplerParameterIuiv(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterIuiv(final int sampler, final int pname, final int[] param, final int param_offset) {
		super.glSamplerParameterIuiv(sampler, pname, param, param_offset);
	}

	@Override
	public void glGetSamplerParameteriv(final int sampler, final int pname, final IntBuffer params) {
		super.glGetSamplerParameteriv(sampler, pname, params);
	}

	@Override
	public void glGetSamplerParameteriv(final int sampler, final int pname, final int[] params,
			final int params_offset) {
		super.glGetSamplerParameteriv(sampler, pname, params, params_offset);
	}

	@Override
	public void glGetSamplerParameterIiv(final int sampler, final int pname, final IntBuffer params) {
		super.glGetSamplerParameterIiv(sampler, pname, params);
	}

	@Override
	public void glGetSamplerParameterIiv(final int sampler, final int pname, final int[] params,
			final int params_offset) {
		super.glGetSamplerParameterIiv(sampler, pname, params, params_offset);
	}

	@Override
	public void glGetSamplerParameterfv(final int sampler, final int pname, final FloatBuffer params) {
		super.glGetSamplerParameterfv(sampler, pname, params);
	}

	@Override
	public void glGetSamplerParameterfv(final int sampler, final int pname, final float[] params,
			final int params_offset) {
		super.glGetSamplerParameterfv(sampler, pname, params, params_offset);
	}

	@Override
	public void glGetSamplerParameterIuiv(final int sampler, final int pname, final IntBuffer params) {
		super.glGetSamplerParameterIuiv(sampler, pname, params);
	}

	@Override
	public void glGetSamplerParameterIuiv(final int sampler, final int pname, final int[] params,
			final int params_offset) {
		super.glGetSamplerParameterIuiv(sampler, pname, params, params_offset);
	}

	@Override
	public void glQueryCounter(final int id, final int target) {
		super.glQueryCounter(id, target);
	}

	@Override
	public void glGetQueryObjecti64v(final int id, final int pname, final LongBuffer params) {
		super.glGetQueryObjecti64v(id, pname, params);
	}

	@Override
	public void glGetQueryObjecti64v(final int id, final int pname, final long[] params, final int params_offset) {
		super.glGetQueryObjecti64v(id, pname, params, params_offset);
	}

	@Override
	public void glGetQueryObjectui64v(final int id, final int pname, final LongBuffer params) {
		super.glGetQueryObjectui64v(id, pname, params);
	}

	@Override
	public void glGetQueryObjectui64v(final int id, final int pname, final long[] params, final int params_offset) {
		super.glGetQueryObjectui64v(id, pname, params, params_offset);
	}

	@Override
	public void glVertexAttribDivisor(final int index, final int divisor) {
		super.glVertexAttribDivisor(index, divisor);
	}

	@Override
	public void glVertexAttribP1ui(final int index, final int type, final boolean normalized, final int value) {
		super.glVertexAttribP1ui(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP1uiv(final int index, final int type, final boolean normalized, final IntBuffer value) {
		super.glVertexAttribP1uiv(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP1uiv(final int index, final int type, final boolean normalized, final int[] value,
			final int value_offset) {
		super.glVertexAttribP1uiv(index, type, normalized, value, value_offset);
	}

	@Override
	public void glVertexAttribP2ui(final int index, final int type, final boolean normalized, final int value) {
		super.glVertexAttribP2ui(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP2uiv(final int index, final int type, final boolean normalized, final IntBuffer value) {
		super.glVertexAttribP2uiv(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP2uiv(final int index, final int type, final boolean normalized, final int[] value,
			final int value_offset) {
		super.glVertexAttribP2uiv(index, type, normalized, value, value_offset);
	}

	@Override
	public void glVertexAttribP3ui(final int index, final int type, final boolean normalized, final int value) {
		super.glVertexAttribP3ui(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP3uiv(final int index, final int type, final boolean normalized, final IntBuffer value) {
		super.glVertexAttribP3uiv(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP3uiv(final int index, final int type, final boolean normalized, final int[] value,
			final int value_offset) {
		super.glVertexAttribP3uiv(index, type, normalized, value, value_offset);
	}

	@Override
	public void glVertexAttribP4ui(final int index, final int type, final boolean normalized, final int value) {
		super.glVertexAttribP4ui(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP4uiv(final int index, final int type, final boolean normalized, final IntBuffer value) {
		super.glVertexAttribP4uiv(index, type, normalized, value);
	}

	@Override
	public void glVertexAttribP4uiv(final int index, final int type, final boolean normalized, final int[] value,
			final int value_offset) {
		super.glVertexAttribP4uiv(index, type, normalized, value, value_offset);
	}

	@Override
	public void glVertexP2ui(final int type, final int value) {
		super.glVertexP2ui(type, value);
	}

	@Override
	public void glVertexP2uiv(final int type, final IntBuffer value) {
		super.glVertexP2uiv(type, value);
	}

	@Override
	public void glVertexP2uiv(final int type, final int[] value, final int value_offset) {
		super.glVertexP2uiv(type, value, value_offset);
	}

	@Override
	public void glVertexP3ui(final int type, final int value) {
		super.glVertexP3ui(type, value);
	}

	@Override
	public void glVertexP3uiv(final int type, final IntBuffer value) {
		super.glVertexP3uiv(type, value);
	}

	@Override
	public void glVertexP3uiv(final int type, final int[] value, final int value_offset) {
		super.glVertexP3uiv(type, value, value_offset);
	}

	@Override
	public void glVertexP4ui(final int type, final int value) {
		super.glVertexP4ui(type, value);
	}

	@Override
	public void glVertexP4uiv(final int type, final IntBuffer value) {
		super.glVertexP4uiv(type, value);
	}

	@Override
	public void glVertexP4uiv(final int type, final int[] value, final int value_offset) {
		super.glVertexP4uiv(type, value, value_offset);
	}

	@Override
	public void glTexCoordP1ui(final int type, final int coords) {
		super.glTexCoordP1ui(type, coords);
	}

	@Override
	public void glTexCoordP1uiv(final int type, final IntBuffer coords) {
		super.glTexCoordP1uiv(type, coords);
	}

	@Override
	public void glTexCoordP1uiv(final int type, final int[] coords, final int coords_offset) {
		super.glTexCoordP1uiv(type, coords, coords_offset);
	}

	@Override
	public void glTexCoordP2ui(final int type, final int coords) {
		super.glTexCoordP2ui(type, coords);
	}

	@Override
	public void glTexCoordP2uiv(final int type, final IntBuffer coords) {
		super.glTexCoordP2uiv(type, coords);
	}

	@Override
	public void glTexCoordP2uiv(final int type, final int[] coords, final int coords_offset) {
		super.glTexCoordP2uiv(type, coords, coords_offset);
	}

	@Override
	public void glTexCoordP3ui(final int type, final int coords) {
		super.glTexCoordP3ui(type, coords);
	}

	@Override
	public void glTexCoordP3uiv(final int type, final IntBuffer coords) {
		super.glTexCoordP3uiv(type, coords);
	}

	@Override
	public void glTexCoordP3uiv(final int type, final int[] coords, final int coords_offset) {
		super.glTexCoordP3uiv(type, coords, coords_offset);
	}

	@Override
	public void glTexCoordP4ui(final int type, final int coords) {
		super.glTexCoordP4ui(type, coords);
	}

	@Override
	public void glTexCoordP4uiv(final int type, final IntBuffer coords) {
		super.glTexCoordP4uiv(type, coords);
	}

	@Override
	public void glTexCoordP4uiv(final int type, final int[] coords, final int coords_offset) {
		super.glTexCoordP4uiv(type, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoordP1ui(final int texture, final int type, final int coords) {
		super.glMultiTexCoordP1ui(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP1uiv(final int texture, final int type, final IntBuffer coords) {
		super.glMultiTexCoordP1uiv(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP1uiv(final int texture, final int type, final int[] coords, final int coords_offset) {
		super.glMultiTexCoordP1uiv(texture, type, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoordP2ui(final int texture, final int type, final int coords) {
		super.glMultiTexCoordP2ui(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP2uiv(final int texture, final int type, final IntBuffer coords) {
		super.glMultiTexCoordP2uiv(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP2uiv(final int texture, final int type, final int[] coords, final int coords_offset) {
		super.glMultiTexCoordP2uiv(texture, type, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoordP3ui(final int texture, final int type, final int coords) {
		super.glMultiTexCoordP3ui(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP3uiv(final int texture, final int type, final IntBuffer coords) {
		super.glMultiTexCoordP3uiv(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP3uiv(final int texture, final int type, final int[] coords, final int coords_offset) {
		super.glMultiTexCoordP3uiv(texture, type, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoordP4ui(final int texture, final int type, final int coords) {
		super.glMultiTexCoordP4ui(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP4uiv(final int texture, final int type, final IntBuffer coords) {
		super.glMultiTexCoordP4uiv(texture, type, coords);
	}

	@Override
	public void glMultiTexCoordP4uiv(final int texture, final int type, final int[] coords, final int coords_offset) {
		super.glMultiTexCoordP4uiv(texture, type, coords, coords_offset);
	}

	@Override
	public void glNormalP3ui(final int type, final int coords) {
		super.glNormalP3ui(type, coords);
	}

	@Override
	public void glNormalP3uiv(final int type, final IntBuffer coords) {
		super.glNormalP3uiv(type, coords);
	}

	@Override
	public void glNormalP3uiv(final int type, final int[] coords, final int coords_offset) {
		super.glNormalP3uiv(type, coords, coords_offset);
	}

	@Override
	public void glColorP3ui(final int type, final int color) {
		super.glColorP3ui(type, color);
	}

	@Override
	public void glColorP3uiv(final int type, final IntBuffer color) {
		super.glColorP3uiv(type, color);
	}

	@Override
	public void glColorP3uiv(final int type, final int[] color, final int color_offset) {
		super.glColorP3uiv(type, color, color_offset);
	}

	@Override
	public void glColorP4ui(final int type, final int color) {
		super.glColorP4ui(type, color);
	}

	@Override
	public void glColorP4uiv(final int type, final IntBuffer color) {
		super.glColorP4uiv(type, color);
	}

	@Override
	public void glColorP4uiv(final int type, final int[] color, final int color_offset) {
		super.glColorP4uiv(type, color, color_offset);
	}

	@Override
	public void glSecondaryColorP3ui(final int type, final int color) {
		super.glSecondaryColorP3ui(type, color);
	}

	@Override
	public void glSecondaryColorP3uiv(final int type, final IntBuffer color) {
		super.glSecondaryColorP3uiv(type, color);
	}

	@Override
	public void glSecondaryColorP3uiv(final int type, final int[] color, final int color_offset) {
		super.glSecondaryColorP3uiv(type, color, color_offset);
	}

	@Override
	public void glMinSampleShading(final float value) {
		super.glMinSampleShading(value);
	}

	@Override
	public void glBlendEquationi(final int buf, final int mode) {
		super.glBlendEquationi(buf, mode);
	}

	@Override
	public void glBlendEquationSeparatei(final int buf, final int modeRGB, final int modeAlpha) {
		super.glBlendEquationSeparatei(buf, modeRGB, modeAlpha);
	}

	@Override
	public void glBlendFunci(final int buf, final int src, final int dst) {
		super.glBlendFunci(buf, src, dst);
	}

	@Override
	public void glBlendFuncSeparatei(final int buf, final int srcRGB, final int dstRGB, final int srcAlpha,
			final int dstAlpha) {
		super.glBlendFuncSeparatei(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glDrawArraysIndirect(final int mode, final Buffer indirect) {
		super.glDrawArraysIndirect(mode, indirect);
	}

	@Override
	public void glDrawArraysIndirect(final int mode, final long indirect_buffer_offset) {
		super.glDrawArraysIndirect(mode, indirect_buffer_offset);
	}

	@Override
	public void glDrawElementsIndirect(final int mode, final int type, final Buffer indirect) {
		super.glDrawElementsIndirect(mode, type, indirect);
	}

	@Override
	public void glDrawElementsIndirect(final int mode, final int type, final long indirect_buffer_offset) {
		super.glDrawElementsIndirect(mode, type, indirect_buffer_offset);
	}

	@Override
	public void glUniform1d(final int location, final double x) {
		super.glUniform1d(location, x);
	}

	@Override
	public void glUniform2d(final int location, final double x, final double y) {
		super.glUniform2d(location, x, y);
	}

	@Override
	public void glUniform3d(final int location, final double x, final double y, final double z) {
		super.glUniform3d(location, x, y, z);
	}

	@Override
	public void glUniform4d(final int location, final double x, final double y, final double z, final double w) {
		super.glUniform4d(location, x, y, z, w);
	}

	@Override
	public void glUniform1dv(final int location, final int count, final DoubleBuffer value) {
		super.glUniform1dv(location, count, value);
	}

	@Override
	public void glUniform1dv(final int location, final int count, final double[] value, final int value_offset) {
		super.glUniform1dv(location, count, value, value_offset);
	}

	@Override
	public void glUniform2dv(final int location, final int count, final DoubleBuffer value) {
		super.glUniform2dv(location, count, value);
	}

	@Override
	public void glUniform2dv(final int location, final int count, final double[] value, final int value_offset) {
		super.glUniform2dv(location, count, value, value_offset);
	}

	@Override
	public void glUniform3dv(final int location, final int count, final DoubleBuffer value) {
		super.glUniform3dv(location, count, value);
	}

	@Override
	public void glUniform3dv(final int location, final int count, final double[] value, final int value_offset) {
		super.glUniform3dv(location, count, value, value_offset);
	}

	@Override
	public void glUniform4dv(final int location, final int count, final DoubleBuffer value) {
		super.glUniform4dv(location, count, value);
	}

	@Override
	public void glUniform4dv(final int location, final int count, final double[] value, final int value_offset) {
		super.glUniform4dv(location, count, value, value_offset);
	}

	@Override
	public void glUniformMatrix2dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix2dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix2dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix3dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix3dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix4dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix4dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix2x3dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix2x3dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2x3dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix2x3dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix2x4dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix2x4dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2x4dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix2x4dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3x2dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix3x2dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x2dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix3x2dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3x4dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix3x4dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x4dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix3x4dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4x2dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix4x2dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4x2dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix4x2dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4x3dv(final int location, final int count, final boolean transpose,
			final DoubleBuffer value) {
		super.glUniformMatrix4x3dv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4x3dv(final int location, final int count, final boolean transpose, final double[] value,
			final int value_offset) {
		super.glUniformMatrix4x3dv(location, count, transpose, value, value_offset);
	}

	@Override
	public void glGetUniformdv(final int program, final int location, final DoubleBuffer params) {
		super.glGetUniformdv(program, location, params);
	}

	@Override
	public void glGetUniformdv(final int program, final int location, final double[] params, final int params_offset) {
		super.glGetUniformdv(program, location, params, params_offset);
	}

	@Override
	public int glGetSubroutineUniformLocation(final int program, final int shadertype, final String name) {
		return super.glGetSubroutineUniformLocation(program, shadertype, name);
	}

	@Override
	public int glGetSubroutineIndex(final int program, final int shadertype, final String name) {
		return super.glGetSubroutineIndex(program, shadertype, name);
	}

	@Override
	public void glGetActiveSubroutineUniformiv(final int program, final int shadertype, final int index,
			final int pname, final IntBuffer values) {
		super.glGetActiveSubroutineUniformiv(program, shadertype, index, pname, values);
	}

	@Override
	public void glGetActiveSubroutineUniformiv(final int program, final int shadertype, final int index,
			final int pname, final int[] values, final int values_offset) {
		super.glGetActiveSubroutineUniformiv(program, shadertype, index, pname, values, values_offset);
	}

	@Override
	public void glGetActiveSubroutineUniformName(final int program, final int shadertype, final int index,
			final int bufsize, final IntBuffer length, final ByteBuffer name) {
		super.glGetActiveSubroutineUniformName(program, shadertype, index, bufsize, length, name);
	}

	@Override
	public void glGetActiveSubroutineUniformName(final int program, final int shadertype, final int index,
			final int bufsize, final int[] length, final int length_offset, final byte[] name, final int name_offset) {
		super.glGetActiveSubroutineUniformName(program, shadertype, index, bufsize, length, length_offset, name,
				name_offset);
	}

	@Override
	public void glGetActiveSubroutineName(final int program, final int shadertype, final int index, final int bufsize,
			final IntBuffer length, final ByteBuffer name) {
		super.glGetActiveSubroutineName(program, shadertype, index, bufsize, length, name);
	}

	@Override
	public void glGetActiveSubroutineName(final int program, final int shadertype, final int index, final int bufsize,
			final int[] length, final int length_offset, final byte[] name, final int name_offset) {
		super.glGetActiveSubroutineName(program, shadertype, index, bufsize, length, length_offset, name, name_offset);
	}

	@Override
	public void glUniformSubroutinesuiv(final int shadertype, final int count, final IntBuffer indices) {
		super.glUniformSubroutinesuiv(shadertype, count, indices);
	}

	@Override
	public void glUniformSubroutinesuiv(final int shadertype, final int count, final int[] indices,
			final int indices_offset) {
		super.glUniformSubroutinesuiv(shadertype, count, indices, indices_offset);
	}

	@Override
	public void glGetUniformSubroutineuiv(final int shadertype, final int location, final IntBuffer params) {
		super.glGetUniformSubroutineuiv(shadertype, location, params);
	}

	@Override
	public void glGetUniformSubroutineuiv(final int shadertype, final int location, final int[] params,
			final int params_offset) {
		super.glGetUniformSubroutineuiv(shadertype, location, params, params_offset);
	}

	@Override
	public void glGetProgramStageiv(final int program, final int shadertype, final int pname, final IntBuffer values) {
		super.glGetProgramStageiv(program, shadertype, pname, values);
	}

	@Override
	public void glGetProgramStageiv(final int program, final int shadertype, final int pname, final int[] values,
			final int values_offset) {
		super.glGetProgramStageiv(program, shadertype, pname, values, values_offset);
	}

	@Override
	public void glPatchParameteri(final int pname, final int value) {
		super.glPatchParameteri(pname, value);
	}

	@Override
	public void glPatchParameterfv(final int pname, final FloatBuffer values) {
		super.glPatchParameterfv(pname, values);
	}

	@Override
	public void glPatchParameterfv(final int pname, final float[] values, final int values_offset) {
		super.glPatchParameterfv(pname, values, values_offset);
	}

	@Override
	public void glBindTransformFeedback(final int target, final int id) {
		super.glBindTransformFeedback(target, id);
	}

	@Override
	public void glDeleteTransformFeedbacks(final int n, final IntBuffer ids) {
		super.glDeleteTransformFeedbacks(n, ids);
	}

	@Override
	public void glDeleteTransformFeedbacks(final int n, final int[] ids, final int ids_offset) {
		super.glDeleteTransformFeedbacks(n, ids, ids_offset);
	}

	@Override
	public void glGenTransformFeedbacks(final int n, final IntBuffer ids) {
		super.glGenTransformFeedbacks(n, ids);
	}

	@Override
	public void glGenTransformFeedbacks(final int n, final int[] ids, final int ids_offset) {
		super.glGenTransformFeedbacks(n, ids, ids_offset);
	}

	@Override
	public boolean glIsTransformFeedback(final int id) {
		return super.glIsTransformFeedback(id);
	}

	@Override
	public void glPauseTransformFeedback() {
		super.glPauseTransformFeedback();
	}

	@Override
	public void glResumeTransformFeedback() {
		super.glResumeTransformFeedback();
	}

	@Override
	public void glDrawTransformFeedback(final int mode, final int id) {
		super.glDrawTransformFeedback(mode, id);
	}

	@Override
	public void glDrawTransformFeedbackStream(final int mode, final int id, final int stream) {
		super.glDrawTransformFeedbackStream(mode, id, stream);
	}

	@Override
	public void glBeginQueryIndexed(final int target, final int index, final int id) {
		super.glBeginQueryIndexed(target, index, id);
	}

	@Override
	public void glEndQueryIndexed(final int target, final int index) {
		super.glEndQueryIndexed(target, index);
	}

	@Override
	public void glGetQueryIndexediv(final int target, final int index, final int pname, final IntBuffer params) {
		super.glGetQueryIndexediv(target, index, pname, params);
	}

	@Override
	public void glGetQueryIndexediv(final int target, final int index, final int pname, final int[] params,
			final int params_offset) {
		super.glGetQueryIndexediv(target, index, pname, params, params_offset);
	}

	@Override
	public void glReleaseShaderCompiler() {
		super.glReleaseShaderCompiler();
	}

	@Override
	public void glShaderBinary(final int count, final IntBuffer shaders, final int binaryformat, final Buffer binary,
			final int length) {
		super.glShaderBinary(count, shaders, binaryformat, binary, length);
	}

	@Override
	public void glShaderBinary(final int count, final int[] shaders, final int shaders_offset, final int binaryformat,
			final Buffer binary, final int length) {
		super.glShaderBinary(count, shaders, shaders_offset, binaryformat, binary, length);
	}

	@Override
	public void glGetShaderPrecisionFormat(final int shadertype, final int precisiontype, final IntBuffer range,
			final IntBuffer precision) {
		super.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision);
	}

	@Override
	public void glGetShaderPrecisionFormat(final int shadertype, final int precisiontype, final int[] range,
			final int range_offset, final int[] precision, final int precision_offset) {
		super.glGetShaderPrecisionFormat(shadertype, precisiontype, range, range_offset, precision, precision_offset);
	}

	@Override
	public void glDepthRangef(final float n, final float f) {
		super.glDepthRangef(n, f);
	}

	@Override
	public void glClearDepthf(final float d) {
		super.glClearDepthf(d);
	}

	@Override
	public void glGetProgramBinary(final int program, final int bufSize, final IntBuffer length,
			final IntBuffer binaryFormat, final Buffer binary) {
		super.glGetProgramBinary(program, bufSize, length, binaryFormat, binary);
	}

	@Override
	public void glGetProgramBinary(final int program, final int bufSize, final int[] length, final int length_offset,
			final int[] binaryFormat, final int binaryFormat_offset, final Buffer binary) {
		super.glGetProgramBinary(program, bufSize, length, length_offset, binaryFormat, binaryFormat_offset, binary);
	}

	@Override
	public void glProgramBinary(final int program, final int binaryFormat, final Buffer binary, final int length) {
		super.glProgramBinary(program, binaryFormat, binary, length);
	}

	@Override
	public void glProgramUniform1d(final int program, final int location, final double v0) {
		super.glProgramUniform1d(program, location, v0);
	}

	@Override
	public void glProgramUniform1dv(final int program, final int location, final int count, final DoubleBuffer value) {
		super.glProgramUniform1dv(program, location, count, value);
	}

	@Override
	public void glProgramUniform1dv(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform1dv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2d(final int program, final int location, final double v0, final double v1) {
		super.glProgramUniform2d(program, location, v0, v1);
	}

	@Override
	public void glProgramUniform2dv(final int program, final int location, final int count, final DoubleBuffer value) {
		super.glProgramUniform2dv(program, location, count, value);
	}

	@Override
	public void glProgramUniform2dv(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform2dv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3d(final int program, final int location, final double v0, final double v1,
			final double v2) {
		super.glProgramUniform3d(program, location, v0, v1, v2);
	}

	@Override
	public void glProgramUniform3dv(final int program, final int location, final int count, final DoubleBuffer value) {
		super.glProgramUniform3dv(program, location, count, value);
	}

	@Override
	public void glProgramUniform3dv(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform3dv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4d(final int program, final int location, final double v0, final double v1,
			final double v2, final double v3) {
		super.glProgramUniform4d(program, location, v0, v1, v2, v3);
	}

	@Override
	public void glProgramUniform4dv(final int program, final int location, final int count, final DoubleBuffer value) {
		super.glProgramUniform4dv(program, location, count, value);
	}

	@Override
	public void glProgramUniform4dv(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform4dv(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x3dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2x3dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x3dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2x3dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x2dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3x2dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x2dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3x2dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x4dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2x4dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x4dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2x4dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x2dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4x2dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x2dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4x2dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x4dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3x4dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x4dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3x4dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x3dv(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4x3dv(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x3dv(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4x3dv(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glVertexAttribL1d(final int index, final double x) {
		super.glVertexAttribL1d(index, x);
	}

	@Override
	public void glVertexAttribL2d(final int index, final double x, final double y) {
		super.glVertexAttribL2d(index, x, y);
	}

	@Override
	public void glVertexAttribL3d(final int index, final double x, final double y, final double z) {
		super.glVertexAttribL3d(index, x, y, z);
	}

	@Override
	public void glVertexAttribL4d(final int index, final double x, final double y, final double z, final double w) {
		super.glVertexAttribL4d(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribL1dv(final int index, final DoubleBuffer v) {
		super.glVertexAttribL1dv(index, v);
	}

	@Override
	public void glVertexAttribL1dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttribL1dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL2dv(final int index, final DoubleBuffer v) {
		super.glVertexAttribL2dv(index, v);
	}

	@Override
	public void glVertexAttribL2dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttribL2dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL3dv(final int index, final DoubleBuffer v) {
		super.glVertexAttribL3dv(index, v);
	}

	@Override
	public void glVertexAttribL3dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttribL3dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL4dv(final int index, final DoubleBuffer v) {
		super.glVertexAttribL4dv(index, v);
	}

	@Override
	public void glVertexAttribL4dv(final int index, final double[] v, final int v_offset) {
		super.glVertexAttribL4dv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribLPointer(final int index, final int size, final int type, final int stride,
			final Buffer pointer) {
		super.glVertexAttribLPointer(index, size, type, stride, pointer);
	}

	@Override
	public void glVertexAttribLPointer(final int index, final int size, final int type, final int stride,
			final long pointer_buffer_offset) {
		super.glVertexAttribLPointer(index, size, type, stride, pointer_buffer_offset);
	}

	@Override
	public void glGetVertexAttribLdv(final int index, final int pname, final DoubleBuffer params) {
		super.glGetVertexAttribLdv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribLdv(final int index, final int pname, final double[] params, final int params_offset) {
		super.glGetVertexAttribLdv(index, pname, params, params_offset);
	}

	@Override
	public void glViewportArrayv(final int first, final int count, final FloatBuffer v) {
		super.glViewportArrayv(first, count, v);
	}

	@Override
	public void glViewportArrayv(final int first, final int count, final float[] v, final int v_offset) {
		super.glViewportArrayv(first, count, v, v_offset);
	}

	@Override
	public void glViewportIndexedf(final int index, final float x, final float y, final float w, final float h) {
		super.glViewportIndexedf(index, x, y, w, h);
	}

	@Override
	public void glViewportIndexedfv(final int index, final FloatBuffer v) {
		super.glViewportIndexedfv(index, v);
	}

	@Override
	public void glViewportIndexedfv(final int index, final float[] v, final int v_offset) {
		super.glViewportIndexedfv(index, v, v_offset);
	}

	@Override
	public void glScissorArrayv(final int first, final int count, final IntBuffer v) {
		super.glScissorArrayv(first, count, v);
	}

	@Override
	public void glScissorArrayv(final int first, final int count, final int[] v, final int v_offset) {
		super.glScissorArrayv(first, count, v, v_offset);
	}

	@Override
	public void glScissorIndexed(final int index, final int left, final int bottom, final int width, final int height) {
		super.glScissorIndexed(index, left, bottom, width, height);
	}

	@Override
	public void glScissorIndexedv(final int index, final IntBuffer v) {
		super.glScissorIndexedv(index, v);
	}

	@Override
	public void glScissorIndexedv(final int index, final int[] v, final int v_offset) {
		super.glScissorIndexedv(index, v, v_offset);
	}

	@Override
	public void glDepthRangeArrayv(final int first, final int count, final DoubleBuffer v) {
		super.glDepthRangeArrayv(first, count, v);
	}

	@Override
	public void glDepthRangeArrayv(final int first, final int count, final double[] v, final int v_offset) {
		super.glDepthRangeArrayv(first, count, v, v_offset);
	}

	@Override
	public void glDepthRangeIndexed(final int index, final double n, final double f) {
		super.glDepthRangeIndexed(index, n, f);
	}

	@Override
	public void glGetFloati_v(final int target, final int index, final FloatBuffer data) {
		super.glGetFloati_v(target, index, data);
	}

	@Override
	public void glGetFloati_v(final int target, final int index, final float[] data, final int data_offset) {
		super.glGetFloati_v(target, index, data, data_offset);
	}

	@Override
	public void glGetDoublei_v(final int target, final int index, final DoubleBuffer data) {
		super.glGetDoublei_v(target, index, data);
	}

	@Override
	public void glGetDoublei_v(final int target, final int index, final double[] data, final int data_offset) {
		super.glGetDoublei_v(target, index, data, data_offset);
	}

	@Override
	public void glDrawArraysInstancedBaseInstance(final int mode, final int first, final int count,
			final int instancecount, final int baseinstance) {
		super.glDrawArraysInstancedBaseInstance(mode, first, count, instancecount, baseinstance);
	}

	@Override
	public void glDrawElementsInstancedBaseInstance(final int mode, final int count, final int type,
			final Buffer indices, final int instancecount, final int baseinstance) {
		super.glDrawElementsInstancedBaseInstance(mode, count, type, indices, instancecount, baseinstance);
	}

	@Override
	public void glDrawElementsInstancedBaseInstance(final int mode, final int count, final int type,
			final long indices_buffer_offset, final int instancecount, final int baseinstance) {
		super.glDrawElementsInstancedBaseInstance(mode, count, type, indices_buffer_offset, instancecount,
				baseinstance);
	}

	@Override
	public void glDrawElementsInstancedBaseVertexBaseInstance(final int mode, final int count, final int type,
			final Buffer indices, final int instancecount, final int basevertex, final int baseinstance) {
		super.glDrawElementsInstancedBaseVertexBaseInstance(mode, count, type, indices, instancecount, basevertex,
				baseinstance);
	}

	@Override
	public void glDrawElementsInstancedBaseVertexBaseInstance(final int mode, final int count, final int type,
			final long indices_buffer_offset, final int instancecount, final int basevertex, final int baseinstance) {
		super.glDrawElementsInstancedBaseVertexBaseInstance(mode, count, type, indices_buffer_offset, instancecount,
				basevertex, baseinstance);
	}

	@Override
	public void glGetInternalformativ(final int target, final int internalformat, final int pname, final int bufSize,
			final IntBuffer params) {
		super.glGetInternalformativ(target, internalformat, pname, bufSize, params);
	}

	@Override
	public void glGetInternalformativ(final int target, final int internalformat, final int pname, final int bufSize,
			final int[] params, final int params_offset) {
		super.glGetInternalformativ(target, internalformat, pname, bufSize, params, params_offset);
	}

	@Override
	public void glGetActiveAtomicCounterBufferiv(final int program, final int bufferIndex, final int pname,
			final IntBuffer params) {
		super.glGetActiveAtomicCounterBufferiv(program, bufferIndex, pname, params);
	}

	@Override
	public void glGetActiveAtomicCounterBufferiv(final int program, final int bufferIndex, final int pname,
			final int[] params, final int params_offset) {
		super.glGetActiveAtomicCounterBufferiv(program, bufferIndex, pname, params, params_offset);
	}

	@Override
	public void glBindImageTexture(final int unit, final int texture, final int level, final boolean layered,
			final int layer, final int access, final int format) {
		super.glBindImageTexture(unit, texture, level, layered, layer, access, format);
	}

	@Override
	public void glMemoryBarrier(final int barriers) {
		super.glMemoryBarrier(barriers);
	}

	@Override
	public void glDrawTransformFeedbackInstanced(final int mode, final int id, final int instancecount) {
		super.glDrawTransformFeedbackInstanced(mode, id, instancecount);
	}

	@Override
	public void glDrawTransformFeedbackStreamInstanced(final int mode, final int id, final int stream,
			final int instancecount) {
		super.glDrawTransformFeedbackStreamInstanced(mode, id, stream, instancecount);
	}

	@Override
	public void glClearBufferData(final int target, final int internalformat, final int format, final int type,
			final Buffer data) {
		super.glClearBufferData(target, internalformat, format, type, data);
	}

	@Override
	public void glClearBufferSubData(final int target, final int internalformat, final long offset, final long size,
			final int format, final int type, final Buffer data) {
		super.glClearBufferSubData(target, internalformat, offset, size, format, type, data);
	}

	@Override
	public void glDispatchCompute(final int num_groups_x, final int num_groups_y, final int num_groups_z) {
		super.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
	}

	@Override
	public void glDispatchComputeIndirect(final long indirect) {
		super.glDispatchComputeIndirect(indirect);
	}

	@Override
	public void glCopyImageSubData(final int srcName, final int srcTarget, final int srcLevel, final int srcX,
			final int srcY, final int srcZ, final int dstName, final int dstTarget, final int dstLevel, final int dstX,
			final int dstY, final int dstZ, final int srcWidth, final int srcHeight, final int srcDepth) {
		super.glCopyImageSubData(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX,
				dstY, dstZ, srcWidth, srcHeight, srcDepth);
	}

	@Override
	public void glFramebufferParameteri(final int target, final int pname, final int param) {
		super.glFramebufferParameteri(target, pname, param);
	}

	@Override
	public void glGetFramebufferParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetFramebufferParameteriv(target, pname, params);
	}

	@Override
	public void glGetFramebufferParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetFramebufferParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glGetInternalformati64v(final int target, final int internalformat, final int pname, final int bufSize,
			final LongBuffer params) {
		super.glGetInternalformati64v(target, internalformat, pname, bufSize, params);
	}

	@Override
	public void glGetInternalformati64v(final int target, final int internalformat, final int pname, final int bufSize,
			final long[] params, final int params_offset) {
		super.glGetInternalformati64v(target, internalformat, pname, bufSize, params, params_offset);
	}

	@Override
	public void glInvalidateTexSubImage(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth) {
		super.glInvalidateTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth);
	}

	@Override
	public void glInvalidateTexImage(final int texture, final int level) {
		super.glInvalidateTexImage(texture, level);
	}

	@Override
	public void glInvalidateBufferSubData(final int buffer, final long offset, final long length) {
		super.glInvalidateBufferSubData(buffer, offset, length);
	}

	@Override
	public void glInvalidateBufferData(final int buffer) {
		super.glInvalidateBufferData(buffer);
	}

	@Override
	public void glInvalidateFramebuffer(final int target, final int numAttachments, final IntBuffer attachments) {
		super.glInvalidateFramebuffer(target, numAttachments, attachments);
	}

	@Override
	public void glInvalidateFramebuffer(final int target, final int numAttachments, final int[] attachments,
			final int attachments_offset) {
		super.glInvalidateFramebuffer(target, numAttachments, attachments, attachments_offset);
	}

	@Override
	public void glInvalidateSubFramebuffer(final int target, final int numAttachments, final IntBuffer attachments,
			final int x, final int y, final int width, final int height) {
		super.glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
	}

	@Override
	public void glInvalidateSubFramebuffer(final int target, final int numAttachments, final int[] attachments,
			final int attachments_offset, final int x, final int y, final int width, final int height) {
		super.glInvalidateSubFramebuffer(target, numAttachments, attachments, attachments_offset, x, y, width, height);
	}

	@Override
	public void glMultiDrawArraysIndirect(final int mode, final Buffer indirect, final int drawcount,
			final int stride) {
		super.glMultiDrawArraysIndirect(mode, indirect, drawcount, stride);
	}

	@Override
	public void glMultiDrawArraysIndirect(final int mode, final long indirect_buffer_offset, final int drawcount,
			final int stride) {
		super.glMultiDrawArraysIndirect(mode, indirect_buffer_offset, drawcount, stride);
	}

	@Override
	public void glMultiDrawElementsIndirect(final int mode, final int type, final Buffer indirect, final int drawcount,
			final int stride) {
		super.glMultiDrawElementsIndirect(mode, type, indirect, drawcount, stride);
	}

	@Override
	public void glGetProgramInterfaceiv(final int program, final int programInterface, final int pname,
			final IntBuffer params) {
		super.glGetProgramInterfaceiv(program, programInterface, pname, params);
	}

	@Override
	public void glGetProgramInterfaceiv(final int program, final int programInterface, final int pname,
			final int[] params, final int params_offset) {
		super.glGetProgramInterfaceiv(program, programInterface, pname, params, params_offset);
	}

	@Override
	public int glGetProgramResourceIndex(final int program, final int programInterface, final ByteBuffer name) {
		return super.glGetProgramResourceIndex(program, programInterface, name);
	}

	@Override
	public int glGetProgramResourceIndex(final int program, final int programInterface, final byte[] name,
			final int name_offset) {
		return super.glGetProgramResourceIndex(program, programInterface, name, name_offset);
	}

	@Override
	public void glGetProgramResourceName(final int program, final int programInterface, final int index,
			final int bufSize, final IntBuffer length, final ByteBuffer name) {
		super.glGetProgramResourceName(program, programInterface, index, bufSize, length, name);
	}

	@Override
	public void glGetProgramResourceName(final int program, final int programInterface, final int index,
			final int bufSize, final int[] length, final int length_offset, final byte[] name, final int name_offset) {
		super.glGetProgramResourceName(program, programInterface, index, bufSize, length, length_offset, name,
				name_offset);
	}

	@Override
	public void glGetProgramResourceiv(final int program, final int programInterface, final int index,
			final int propCount, final IntBuffer props, final int bufSize, final IntBuffer length,
			final IntBuffer params) {
		super.glGetProgramResourceiv(program, programInterface, index, propCount, props, bufSize, length, params);
	}

	@Override
	public void glGetProgramResourceiv(final int program, final int programInterface, final int index,
			final int propCount, final int[] props, final int props_offset, final int bufSize, final int[] length,
			final int length_offset, final int[] params, final int params_offset) {
		super.glGetProgramResourceiv(program, programInterface, index, propCount, props, props_offset, bufSize, length,
				length_offset, params, params_offset);
	}

	@Override
	public int glGetProgramResourceLocation(final int program, final int programInterface, final ByteBuffer name) {
		return super.glGetProgramResourceLocation(program, programInterface, name);
	}

	@Override
	public int glGetProgramResourceLocation(final int program, final int programInterface, final byte[] name,
			final int name_offset) {
		return super.glGetProgramResourceLocation(program, programInterface, name, name_offset);
	}

	@Override
	public int glGetProgramResourceLocationIndex(final int program, final int programInterface, final ByteBuffer name) {
		return super.glGetProgramResourceLocationIndex(program, programInterface, name);
	}

	@Override
	public int glGetProgramResourceLocationIndex(final int program, final int programInterface, final byte[] name,
			final int name_offset) {
		return super.glGetProgramResourceLocationIndex(program, programInterface, name, name_offset);
	}

	@Override
	public void glShaderStorageBlockBinding(final int program, final int storageBlockIndex,
			final int storageBlockBinding) {
		super.glShaderStorageBlockBinding(program, storageBlockIndex, storageBlockBinding);
	}

	@Override
	public void glTexBufferRange(final int target, final int internalformat, final int buffer, final long offset,
			final long size) {
		super.glTexBufferRange(target, internalformat, buffer, offset, size);
	}

	@Override
	public void glTexStorage2DMultisample(final int target, final int samples, final int internalformat,
			final int width, final int height, final boolean fixedsamplelocations) {
		super.glTexStorage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations);
	}

	@Override
	public void glTexStorage3DMultisample(final int target, final int samples, final int internalformat,
			final int width, final int height, final int depth, final boolean fixedsamplelocations) {
		super.glTexStorage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations);
	}

	@Override
	public void glTextureView(final int texture, final int target, final int origtexture, final int internalformat,
			final int minlevel, final int numlevels, final int minlayer, final int numlayers) {
		super.glTextureView(texture, target, origtexture, internalformat, minlevel, numlevels, minlayer, numlayers);
	}

	@Override
	public void glBindVertexBuffer(final int bindingindex, final int buffer, final long offset, final int stride) {
		super.glBindVertexBuffer(bindingindex, buffer, offset, stride);
	}

	@Override
	public void glVertexAttribFormat(final int attribindex, final int size, final int type, final boolean normalized,
			final int relativeoffset) {
		super.glVertexAttribFormat(attribindex, size, type, normalized, relativeoffset);
	}

	@Override
	public void glVertexAttribIFormat(final int attribindex, final int size, final int type, final int relativeoffset) {
		super.glVertexAttribIFormat(attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexAttribLFormat(final int attribindex, final int size, final int type, final int relativeoffset) {
		super.glVertexAttribLFormat(attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexAttribBinding(final int attribindex, final int bindingindex) {
		super.glVertexAttribBinding(attribindex, bindingindex);
	}

	@Override
	public void glVertexBindingDivisor(final int bindingindex, final int divisor) {
		super.glVertexBindingDivisor(bindingindex, divisor);
	}

	@Override
	public void glDebugMessageControl(final int source, final int type, final int severity, final int count,
			final IntBuffer ids, final boolean enabled) {
		super.glDebugMessageControl(source, type, severity, count, ids, enabled);
	}

	@Override
	public void glDebugMessageControl(final int source, final int type, final int severity, final int count,
			final int[] ids, final int ids_offset, final boolean enabled) {
		super.glDebugMessageControl(source, type, severity, count, ids, ids_offset, enabled);
	}

	@Override
	public void glDebugMessageInsert(final int source, final int type, final int id, final int severity,
			final int length, final String buf) {
		super.glDebugMessageInsert(source, type, id, severity, length, buf);
	}

	@Override
	public int glGetDebugMessageLog(final int count, final int bufSize, final IntBuffer sources, final IntBuffer types,
			final IntBuffer ids, final IntBuffer severities, final IntBuffer lengths, final ByteBuffer messageLog) {
		return super.glGetDebugMessageLog(count, bufSize, sources, types, ids, severities, lengths, messageLog);
	}

	@Override
	public int glGetDebugMessageLog(final int count, final int bufSize, final int[] sources, final int sources_offset,
			final int[] types, final int types_offset, final int[] ids, final int ids_offset, final int[] severities,
			final int severities_offset, final int[] lengths, final int lengths_offset, final byte[] messageLog,
			final int messageLog_offset) {
		return super.glGetDebugMessageLog(count, bufSize, sources, sources_offset, types, types_offset, ids, ids_offset,
				severities, severities_offset, lengths, lengths_offset, messageLog, messageLog_offset);
	}

	@Override
	public void glPushDebugGroup(final int source, final int id, final int length, final ByteBuffer message) {
		super.glPushDebugGroup(source, id, length, message);
	}

	@Override
	public void glPushDebugGroup(final int source, final int id, final int length, final byte[] message,
			final int message_offset) {
		super.glPushDebugGroup(source, id, length, message, message_offset);
	}

	@Override
	public void glPopDebugGroup() {
		super.glPopDebugGroup();
	}

	@Override
	public void glObjectLabel(final int identifier, final int name, final int length, final ByteBuffer label) {
		super.glObjectLabel(identifier, name, length, label);
	}

	@Override
	public void glObjectLabel(final int identifier, final int name, final int length, final byte[] label,
			final int label_offset) {
		super.glObjectLabel(identifier, name, length, label, label_offset);
	}

	@Override
	public void glGetObjectLabel(final int identifier, final int name, final int bufSize, final IntBuffer length,
			final ByteBuffer label) {
		super.glGetObjectLabel(identifier, name, bufSize, length, label);
	}

	@Override
	public void glGetObjectLabel(final int identifier, final int name, final int bufSize, final int[] length,
			final int length_offset, final byte[] label, final int label_offset) {
		super.glGetObjectLabel(identifier, name, bufSize, length, length_offset, label, label_offset);
	}

	@Override
	public void glObjectPtrLabel(final Buffer ptr, final int length, final ByteBuffer label) {
		super.glObjectPtrLabel(ptr, length, label);
	}

	@Override
	public void glObjectPtrLabel(final Buffer ptr, final int length, final byte[] label, final int label_offset) {
		super.glObjectPtrLabel(ptr, length, label, label_offset);
	}

	@Override
	public void glGetObjectPtrLabel(final Buffer ptr, final int bufSize, final IntBuffer length,
			final ByteBuffer label) {
		super.glGetObjectPtrLabel(ptr, bufSize, length, label);
	}

	@Override
	public void glGetObjectPtrLabel(final Buffer ptr, final int bufSize, final int[] length, final int length_offset,
			final byte[] label, final int label_offset) {
		super.glGetObjectPtrLabel(ptr, bufSize, length, length_offset, label, label_offset);
	}

	@Override
	public void glClearTexImage(final int texture, final int level, final int format, final int type,
			final Buffer data) {
		super.glClearTexImage(texture, level, format, type, data);
	}

	@Override
	public void glClearTexSubImage(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final Buffer data) {
		super.glClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, data);
	}

	@Override
	public void glBindBuffersBase(final int target, final int first, final int count, final IntBuffer buffers) {
		super.glBindBuffersBase(target, first, count, buffers);
	}

	@Override
	public void glBindBuffersBase(final int target, final int first, final int count, final int[] buffers,
			final int buffers_offset) {
		super.glBindBuffersBase(target, first, count, buffers, buffers_offset);
	}

	@Override
	public void glBindBuffersRange(final int target, final int first, final int count, final IntBuffer buffers,
			final PointerBuffer offsets, final PointerBuffer sizes) {
		super.glBindBuffersRange(target, first, count, buffers, offsets, sizes);
	}

	@Override
	public void glBindBuffersRange(final int target, final int first, final int count, final int[] buffers,
			final int buffers_offset, final PointerBuffer offsets, final PointerBuffer sizes) {
		super.glBindBuffersRange(target, first, count, buffers, buffers_offset, offsets, sizes);
	}

	@Override
	public void glBindTextures(final int first, final int count, final IntBuffer textures) {
		super.glBindTextures(first, count, textures);
	}

	@Override
	public void glBindTextures(final int first, final int count, final int[] textures, final int textures_offset) {
		super.glBindTextures(first, count, textures, textures_offset);
	}

	@Override
	public void glBindSamplers(final int first, final int count, final IntBuffer samplers) {
		super.glBindSamplers(first, count, samplers);
	}

	@Override
	public void glBindSamplers(final int first, final int count, final int[] samplers, final int samplers_offset) {
		super.glBindSamplers(first, count, samplers, samplers_offset);
	}

	@Override
	public void glBindImageTextures(final int first, final int count, final IntBuffer textures) {
		super.glBindImageTextures(first, count, textures);
	}

	@Override
	public void glBindImageTextures(final int first, final int count, final int[] textures, final int textures_offset) {
		super.glBindImageTextures(first, count, textures, textures_offset);
	}

	@Override
	public void glBindVertexBuffers(final int first, final int count, final IntBuffer buffers,
			final PointerBuffer offsets, final IntBuffer strides) {
		super.glBindVertexBuffers(first, count, buffers, offsets, strides);
	}

	@Override
	public void glBindVertexBuffers(final int first, final int count, final int[] buffers, final int buffers_offset,
			final PointerBuffer offsets, final int[] strides, final int strides_offset) {
		super.glBindVertexBuffers(first, count, buffers, buffers_offset, offsets, strides, strides_offset);
	}

	@Override
	public void glClipControl(final int origin, final int depth) {
		super.glClipControl(origin, depth);
	}

	@Override
	public void glCreateTransformFeedbacks(final int n, final IntBuffer ids) {
		super.glCreateTransformFeedbacks(n, ids);
	}

	@Override
	public void glCreateTransformFeedbacks(final int n, final int[] ids, final int ids_offset) {
		super.glCreateTransformFeedbacks(n, ids, ids_offset);
	}

	@Override
	public void glTransformFeedbackBufferBase(final int xfb, final int index, final int buffer) {
		super.glTransformFeedbackBufferBase(xfb, index, buffer);
	}

	@Override
	public void glTransformFeedbackBufferRange(final int xfb, final int index, final int buffer, final long offset,
			final long size) {
		super.glTransformFeedbackBufferRange(xfb, index, buffer, offset, size);
	}

	@Override
	public void glGetTransformFeedbackiv(final int xfb, final int pname, final IntBuffer param) {
		super.glGetTransformFeedbackiv(xfb, pname, param);
	}

	@Override
	public void glGetTransformFeedbackiv(final int xfb, final int pname, final int[] param, final int param_offset) {
		super.glGetTransformFeedbackiv(xfb, pname, param, param_offset);
	}

	@Override
	public void glGetTransformFeedbacki_v(final int xfb, final int pname, final int index, final IntBuffer param) {
		super.glGetTransformFeedbacki_v(xfb, pname, index, param);
	}

	@Override
	public void glGetTransformFeedbacki_v(final int xfb, final int pname, final int index, final int[] param,
			final int param_offset) {
		super.glGetTransformFeedbacki_v(xfb, pname, index, param, param_offset);
	}

	@Override
	public void glGetTransformFeedbacki64_v(final int xfb, final int pname, final int index, final LongBuffer param) {
		super.glGetTransformFeedbacki64_v(xfb, pname, index, param);
	}

	@Override
	public void glGetTransformFeedbacki64_v(final int xfb, final int pname, final int index, final long[] param,
			final int param_offset) {
		super.glGetTransformFeedbacki64_v(xfb, pname, index, param, param_offset);
	}

	@Override
	public void glCreateBuffers(final int n, final IntBuffer buffers) {
		super.glCreateBuffers(n, buffers);
	}

	@Override
	public void glCreateBuffers(final int n, final int[] buffers, final int buffers_offset) {
		super.glCreateBuffers(n, buffers, buffers_offset);
	}

	@Override
	public void glNamedBufferSubData(final int buffer, final long offset, final long size, final Buffer data) {
		super.glNamedBufferSubData(buffer, offset, size, data);
	}

	@Override
	public void glCopyNamedBufferSubData(final int readBuffer, final int writeBuffer, final long readOffset,
			final long writeOffset, final long size) {
		super.glCopyNamedBufferSubData(readBuffer, writeBuffer, readOffset, writeOffset, size);
	}

	@Override
	public void glClearNamedBufferData(final int buffer, final int internalformat, final int format, final int type,
			final Buffer data) {
		super.glClearNamedBufferData(buffer, internalformat, format, type, data);
	}

	@Override
	public void glClearNamedBufferSubData(final int buffer, final int internalformat, final long offset,
			final long size, final int format, final int type, final Buffer data) {
		super.glClearNamedBufferSubData(buffer, internalformat, offset, size, format, type, data);
	}

	@Override
	public void glFlushMappedNamedBufferRange(final int buffer, final long offset, final long length) {
		super.glFlushMappedNamedBufferRange(buffer, offset, length);
	}

	@Override
	public void glGetNamedBufferParameteriv(final int buffer, final int pname, final IntBuffer params) {
		super.glGetNamedBufferParameteriv(buffer, pname, params);
	}

	@Override
	public void glGetNamedBufferParameteriv(final int buffer, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedBufferParameteriv(buffer, pname, params, params_offset);
	}

	@Override
	public void glGetNamedBufferParameteri64v(final int buffer, final int pname, final LongBuffer params) {
		super.glGetNamedBufferParameteri64v(buffer, pname, params);
	}

	@Override
	public void glGetNamedBufferParameteri64v(final int buffer, final int pname, final long[] params,
			final int params_offset) {
		super.glGetNamedBufferParameteri64v(buffer, pname, params, params_offset);
	}

	@Override
	public void glGetNamedBufferPointerv(final int buffer, final int pname, final PointerBuffer params) {
		super.glGetNamedBufferPointerv(buffer, pname, params);
	}

	@Override
	public void glGetNamedBufferSubData(final int buffer, final long offset, final long size, final Buffer data) {
		super.glGetNamedBufferSubData(buffer, offset, size, data);
	}

	@Override
	public void glCreateFramebuffers(final int n, final IntBuffer framebuffers) {
		super.glCreateFramebuffers(n, framebuffers);
	}

	@Override
	public void glCreateFramebuffers(final int n, final int[] framebuffers, final int framebuffers_offset) {
		super.glCreateFramebuffers(n, framebuffers, framebuffers_offset);
	}

	@Override
	public void glNamedFramebufferRenderbuffer(final int framebuffer, final int attachment,
			final int renderbuffertarget, final int renderbuffer) {
		super.glNamedFramebufferRenderbuffer(framebuffer, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glNamedFramebufferParameteri(final int framebuffer, final int pname, final int param) {
		super.glNamedFramebufferParameteri(framebuffer, pname, param);
	}

	@Override
	public void glNamedFramebufferTexture(final int framebuffer, final int attachment, final int texture,
			final int level) {
		super.glNamedFramebufferTexture(framebuffer, attachment, texture, level);
	}

	@Override
	public void glNamedFramebufferTextureLayer(final int framebuffer, final int attachment, final int texture,
			final int level, final int layer) {
		super.glNamedFramebufferTextureLayer(framebuffer, attachment, texture, level, layer);
	}

	@Override
	public void glNamedFramebufferDrawBuffer(final int framebuffer, final int buf) {
		super.glNamedFramebufferDrawBuffer(framebuffer, buf);
	}

	@Override
	public void glNamedFramebufferDrawBuffers(final int framebuffer, final int n, final IntBuffer bufs) {
		super.glNamedFramebufferDrawBuffers(framebuffer, n, bufs);
	}

	@Override
	public void glNamedFramebufferDrawBuffers(final int framebuffer, final int n, final int[] bufs,
			final int bufs_offset) {
		super.glNamedFramebufferDrawBuffers(framebuffer, n, bufs, bufs_offset);
	}

	@Override
	public void glNamedFramebufferReadBuffer(final int framebuffer, final int src) {
		super.glNamedFramebufferReadBuffer(framebuffer, src);
	}

	@Override
	public void glInvalidateNamedFramebufferData(final int framebuffer, final int numAttachments,
			final IntBuffer attachments) {
		super.glInvalidateNamedFramebufferData(framebuffer, numAttachments, attachments);
	}

	@Override
	public void glInvalidateNamedFramebufferData(final int framebuffer, final int numAttachments,
			final int[] attachments, final int attachments_offset) {
		super.glInvalidateNamedFramebufferData(framebuffer, numAttachments, attachments, attachments_offset);
	}

	@Override
	public void glInvalidateNamedFramebufferSubData(final int framebuffer, final int numAttachments,
			final IntBuffer attachments, final int x, final int y, final int width, final int height) {
		super.glInvalidateNamedFramebufferSubData(framebuffer, numAttachments, attachments, x, y, width, height);
	}

	@Override
	public void glInvalidateNamedFramebufferSubData(final int framebuffer, final int numAttachments,
			final int[] attachments, final int attachments_offset, final int x, final int y, final int width,
			final int height) {
		super.glInvalidateNamedFramebufferSubData(framebuffer, numAttachments, attachments, attachments_offset, x, y,
				width, height);
	}

	@Override
	public void glClearNamedFramebufferiv(final int framebuffer, final int buffer, final int drawbuffer,
			final IntBuffer value) {
		super.glClearNamedFramebufferiv(framebuffer, buffer, drawbuffer, value);
	}

	@Override
	public void glClearNamedFramebufferiv(final int framebuffer, final int buffer, final int drawbuffer,
			final int[] value, final int value_offset) {
		super.glClearNamedFramebufferiv(framebuffer, buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearNamedFramebufferuiv(final int framebuffer, final int buffer, final int drawbuffer,
			final IntBuffer value) {
		super.glClearNamedFramebufferuiv(framebuffer, buffer, drawbuffer, value);
	}

	@Override
	public void glClearNamedFramebufferuiv(final int framebuffer, final int buffer, final int drawbuffer,
			final int[] value, final int value_offset) {
		super.glClearNamedFramebufferuiv(framebuffer, buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearNamedFramebufferfv(final int framebuffer, final int buffer, final int drawbuffer,
			final FloatBuffer value) {
		super.glClearNamedFramebufferfv(framebuffer, buffer, drawbuffer, value);
	}

	@Override
	public void glClearNamedFramebufferfv(final int framebuffer, final int buffer, final int drawbuffer,
			final float[] value, final int value_offset) {
		super.glClearNamedFramebufferfv(framebuffer, buffer, drawbuffer, value, value_offset);
	}

	@Override
	public void glClearNamedFramebufferfi(final int framebuffer, final int buffer, final float depth,
			final int stencil) {
		super.glClearNamedFramebufferfi(framebuffer, buffer, depth, stencil);
	}

	@Override
	public void glBlitNamedFramebuffer(final int readFramebuffer, final int drawFramebuffer, final int srcX0,
			final int srcY0, final int srcX1, final int srcY1, final int dstX0, final int dstY0, final int dstX1,
			final int dstY1, final int mask, final int filter) {
		super.glBlitNamedFramebuffer(readFramebuffer, drawFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1,
				dstY1, mask, filter);
	}

	@Override
	public int glCheckNamedFramebufferStatus(final int framebuffer, final int target) {
		return super.glCheckNamedFramebufferStatus(framebuffer, target);
	}

	@Override
	public void glGetNamedFramebufferParameteriv(final int framebuffer, final int pname, final IntBuffer param) {
		super.glGetNamedFramebufferParameteriv(framebuffer, pname, param);
	}

	@Override
	public void glGetNamedFramebufferParameteriv(final int framebuffer, final int pname, final int[] param,
			final int param_offset) {
		super.glGetNamedFramebufferParameteriv(framebuffer, pname, param, param_offset);
	}

	@Override
	public void glGetNamedFramebufferAttachmentParameteriv(final int framebuffer, final int attachment, final int pname,
			final IntBuffer params) {
		super.glGetNamedFramebufferAttachmentParameteriv(framebuffer, attachment, pname, params);
	}

	@Override
	public void glGetNamedFramebufferAttachmentParameteriv(final int framebuffer, final int attachment, final int pname,
			final int[] params, final int params_offset) {
		super.glGetNamedFramebufferAttachmentParameteriv(framebuffer, attachment, pname, params, params_offset);
	}

	@Override
	public void glCreateRenderbuffers(final int n, final IntBuffer renderbuffers) {
		super.glCreateRenderbuffers(n, renderbuffers);
	}

	@Override
	public void glCreateRenderbuffers(final int n, final int[] renderbuffers, final int renderbuffers_offset) {
		super.glCreateRenderbuffers(n, renderbuffers, renderbuffers_offset);
	}

	@Override
	public void glNamedRenderbufferStorage(final int renderbuffer, final int internalformat, final int width,
			final int height) {
		super.glNamedRenderbufferStorage(renderbuffer, internalformat, width, height);
	}

	@Override
	public void glNamedRenderbufferStorageMultisample(final int renderbuffer, final int samples,
			final int internalformat, final int width, final int height) {
		super.glNamedRenderbufferStorageMultisample(renderbuffer, samples, internalformat, width, height);
	}

	@Override
	public void glGetNamedRenderbufferParameteriv(final int renderbuffer, final int pname, final IntBuffer params) {
		super.glGetNamedRenderbufferParameteriv(renderbuffer, pname, params);
	}

	@Override
	public void glGetNamedRenderbufferParameteriv(final int renderbuffer, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedRenderbufferParameteriv(renderbuffer, pname, params, params_offset);
	}

	@Override
	public void glCreateTextures(final int target, final int n, final IntBuffer textures) {
		super.glCreateTextures(target, n, textures);
	}

	@Override
	public void glCreateTextures(final int target, final int n, final int[] textures, final int textures_offset) {
		super.glCreateTextures(target, n, textures, textures_offset);
	}

	@Override
	public void glTextureBuffer(final int texture, final int internalformat, final int buffer) {
		super.glTextureBuffer(texture, internalformat, buffer);
	}

	@Override
	public void glTextureBufferRange(final int texture, final int internalformat, final int buffer, final long offset,
			final long size) {
		super.glTextureBufferRange(texture, internalformat, buffer, offset, size);
	}

	@Override
	public void glTextureStorage1D(final int texture, final int levels, final int internalformat, final int width) {
		super.glTextureStorage1D(texture, levels, internalformat, width);
	}

	@Override
	public void glTextureStorage2D(final int texture, final int levels, final int internalformat, final int width,
			final int height) {
		super.glTextureStorage2D(texture, levels, internalformat, width, height);
	}

	@Override
	public void glTextureStorage3D(final int texture, final int levels, final int internalformat, final int width,
			final int height, final int depth) {
		super.glTextureStorage3D(texture, levels, internalformat, width, height, depth);
	}

	@Override
	public void glTextureStorage2DMultisample(final int texture, final int samples, final int internalformat,
			final int width, final int height, final boolean fixedsamplelocations) {
		super.glTextureStorage2DMultisample(texture, samples, internalformat, width, height, fixedsamplelocations);
	}

	@Override
	public void glTextureStorage3DMultisample(final int texture, final int samples, final int internalformat,
			final int width, final int height, final int depth, final boolean fixedsamplelocations) {
		super.glTextureStorage3DMultisample(texture, samples, internalformat, width, height, depth,
				fixedsamplelocations);
	}

	@Override
	public void glTextureSubImage1D(final int texture, final int level, final int xoffset, final int width,
			final int format, final int type, final Buffer pixels) {
		super.glTextureSubImage1D(texture, level, xoffset, width, format, type, pixels);
	}

	@Override
	public void glTextureSubImage1D(final int texture, final int level, final int xoffset, final int width,
			final int format, final int type, final long pixels_buffer_offset) {
		super.glTextureSubImage1D(texture, level, xoffset, width, format, type, pixels_buffer_offset);
	}

	@Override
	public void glTextureSubImage2D(final int texture, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int type, final Buffer pixels) {
		super.glTextureSubImage2D(texture, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glTextureSubImage2D(final int texture, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int type, final long pixels_buffer_offset) {
		super.glTextureSubImage2D(texture, level, xoffset, yoffset, width, height, format, type, pixels_buffer_offset);
	}

	@Override
	public void glTextureSubImage3D(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final Buffer pixels) {
		super.glTextureSubImage3D(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type,
				pixels);
	}

	@Override
	public void glTextureSubImage3D(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTextureSubImage3D(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glCompressedTextureSubImage1D(final int texture, final int level, final int xoffset, final int width,
			final int format, final int imageSize, final Buffer data) {
		super.glCompressedTextureSubImage1D(texture, level, xoffset, width, format, imageSize, data);
	}

	@Override
	public void glCompressedTextureSubImage2D(final int texture, final int level, final int xoffset, final int yoffset,
			final int width, final int height, final int format, final int imageSize, final Buffer data) {
		super.glCompressedTextureSubImage2D(texture, level, xoffset, yoffset, width, height, format, imageSize, data);
	}

	@Override
	public void glCompressedTextureSubImage3D(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format,
			final int imageSize, final Buffer data) {
		super.glCompressedTextureSubImage3D(texture, level, xoffset, yoffset, zoffset, width, height, depth, format,
				imageSize, data);
	}

	@Override
	public void glCopyTextureSubImage1D(final int texture, final int level, final int xoffset, final int x, final int y,
			final int width) {
		super.glCopyTextureSubImage1D(texture, level, xoffset, x, y, width);
	}

	@Override
	public void glCopyTextureSubImage2D(final int texture, final int level, final int xoffset, final int yoffset,
			final int x, final int y, final int width, final int height) {
		super.glCopyTextureSubImage2D(texture, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glCopyTextureSubImage3D(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int x, final int y, final int width, final int height) {
		super.glCopyTextureSubImage3D(texture, level, xoffset, yoffset, zoffset, x, y, width, height);
	}

	@Override
	public void glTextureParameterf(final int texture, final int pname, final float param) {
		super.glTextureParameterf(texture, pname, param);
	}

	@Override
	public void glTextureParameterfv(final int texture, final int pname, final FloatBuffer param) {
		super.glTextureParameterfv(texture, pname, param);
	}

	@Override
	public void glTextureParameterfv(final int texture, final int pname, final float[] param, final int param_offset) {
		super.glTextureParameterfv(texture, pname, param, param_offset);
	}

	@Override
	public void glTextureParameteri(final int texture, final int pname, final int param) {
		super.glTextureParameteri(texture, pname, param);
	}

	@Override
	public void glTextureParameterIiv(final int texture, final int pname, final IntBuffer params) {
		super.glTextureParameterIiv(texture, pname, params);
	}

	@Override
	public void glTextureParameterIiv(final int texture, final int pname, final int[] params, final int params_offset) {
		super.glTextureParameterIiv(texture, pname, params, params_offset);
	}

	@Override
	public void glTextureParameterIuiv(final int texture, final int pname, final IntBuffer params) {
		super.glTextureParameterIuiv(texture, pname, params);
	}

	@Override
	public void glTextureParameterIuiv(final int texture, final int pname, final int[] params,
			final int params_offset) {
		super.glTextureParameterIuiv(texture, pname, params, params_offset);
	}

	@Override
	public void glTextureParameteriv(final int texture, final int pname, final IntBuffer param) {
		super.glTextureParameteriv(texture, pname, param);
	}

	@Override
	public void glTextureParameteriv(final int texture, final int pname, final int[] param, final int param_offset) {
		super.glTextureParameteriv(texture, pname, param, param_offset);
	}

	@Override
	public void glGenerateTextureMipmap(final int texture) {
		super.glGenerateTextureMipmap(texture);
	}

	@Override
	public void glBindTextureUnit(final int unit, final int texture) {
		super.glBindTextureUnit(unit, texture);
	}

	@Override
	public void glGetTextureImage(final int texture, final int level, final int format, final int type,
			final int bufSize, final Buffer pixels) {
		super.glGetTextureImage(texture, level, format, type, bufSize, pixels);
	}

	@Override
	public void glGetCompressedTextureImage(final int texture, final int level, final int bufSize,
			final Buffer pixels) {
		super.glGetCompressedTextureImage(texture, level, bufSize, pixels);
	}

	@Override
	public void glGetTextureLevelParameterfv(final int texture, final int level, final int pname,
			final FloatBuffer params) {
		super.glGetTextureLevelParameterfv(texture, level, pname, params);
	}

	@Override
	public void glGetTextureLevelParameterfv(final int texture, final int level, final int pname, final float[] params,
			final int params_offset) {
		super.glGetTextureLevelParameterfv(texture, level, pname, params, params_offset);
	}

	@Override
	public void glGetTextureLevelParameteriv(final int texture, final int level, final int pname,
			final IntBuffer params) {
		super.glGetTextureLevelParameteriv(texture, level, pname, params);
	}

	@Override
	public void glGetTextureLevelParameteriv(final int texture, final int level, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureLevelParameteriv(texture, level, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterfv(final int texture, final int pname, final FloatBuffer params) {
		super.glGetTextureParameterfv(texture, pname, params);
	}

	@Override
	public void glGetTextureParameterfv(final int texture, final int pname, final float[] params,
			final int params_offset) {
		super.glGetTextureParameterfv(texture, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterIiv(final int texture, final int pname, final IntBuffer params) {
		super.glGetTextureParameterIiv(texture, pname, params);
	}

	@Override
	public void glGetTextureParameterIiv(final int texture, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameterIiv(texture, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterIuiv(final int texture, final int pname, final IntBuffer params) {
		super.glGetTextureParameterIuiv(texture, pname, params);
	}

	@Override
	public void glGetTextureParameterIuiv(final int texture, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameterIuiv(texture, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameteriv(final int texture, final int pname, final IntBuffer params) {
		super.glGetTextureParameteriv(texture, pname, params);
	}

	@Override
	public void glGetTextureParameteriv(final int texture, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameteriv(texture, pname, params, params_offset);
	}

	@Override
	public void glCreateVertexArrays(final int n, final IntBuffer arrays) {
		super.glCreateVertexArrays(n, arrays);
	}

	@Override
	public void glCreateVertexArrays(final int n, final int[] arrays, final int arrays_offset) {
		super.glCreateVertexArrays(n, arrays, arrays_offset);
	}

	@Override
	public void glDisableVertexArrayAttrib(final int vaobj, final int index) {
		super.glDisableVertexArrayAttrib(vaobj, index);
	}

	@Override
	public void glEnableVertexArrayAttrib(final int vaobj, final int index) {
		super.glEnableVertexArrayAttrib(vaobj, index);
	}

	@Override
	public void glVertexArrayElementBuffer(final int vaobj, final int buffer) {
		super.glVertexArrayElementBuffer(vaobj, buffer);
	}

	@Override
	public void glVertexArrayVertexBuffer(final int vaobj, final int bindingindex, final int buffer, final long offset,
			final int stride) {
		super.glVertexArrayVertexBuffer(vaobj, bindingindex, buffer, offset, stride);
	}

	@Override
	public void glVertexArrayVertexBuffers(final int vaobj, final int first, final int count, final IntBuffer buffers,
			final PointerBuffer offsets, final IntBuffer strides) {
		super.glVertexArrayVertexBuffers(vaobj, first, count, buffers, offsets, strides);
	}

	@Override
	public void glVertexArrayVertexBuffers(final int vaobj, final int first, final int count, final int[] buffers,
			final int buffers_offset, final PointerBuffer offsets, final int[] strides, final int strides_offset) {
		super.glVertexArrayVertexBuffers(vaobj, first, count, buffers, buffers_offset, offsets, strides,
				strides_offset);
	}

	@Override
	public void glVertexArrayAttribBinding(final int vaobj, final int attribindex, final int bindingindex) {
		super.glVertexArrayAttribBinding(vaobj, attribindex, bindingindex);
	}

	@Override
	public void glVertexArrayAttribFormat(final int vaobj, final int attribindex, final int size, final int type,
			final boolean normalized, final int relativeoffset) {
		super.glVertexArrayAttribFormat(vaobj, attribindex, size, type, normalized, relativeoffset);
	}

	@Override
	public void glVertexArrayAttribIFormat(final int vaobj, final int attribindex, final int size, final int type,
			final int relativeoffset) {
		super.glVertexArrayAttribIFormat(vaobj, attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexArrayAttribLFormat(final int vaobj, final int attribindex, final int size, final int type,
			final int relativeoffset) {
		super.glVertexArrayAttribLFormat(vaobj, attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexArrayBindingDivisor(final int vaobj, final int bindingindex, final int divisor) {
		super.glVertexArrayBindingDivisor(vaobj, bindingindex, divisor);
	}

	@Override
	public void glGetVertexArrayiv(final int vaobj, final int pname, final IntBuffer param) {
		super.glGetVertexArrayiv(vaobj, pname, param);
	}

	@Override
	public void glGetVertexArrayiv(final int vaobj, final int pname, final int[] param, final int param_offset) {
		super.glGetVertexArrayiv(vaobj, pname, param, param_offset);
	}

	@Override
	public void glGetVertexArrayIndexediv(final int vaobj, final int index, final int pname, final IntBuffer param) {
		super.glGetVertexArrayIndexediv(vaobj, index, pname, param);
	}

	@Override
	public void glGetVertexArrayIndexediv(final int vaobj, final int index, final int pname, final int[] param,
			final int param_offset) {
		super.glGetVertexArrayIndexediv(vaobj, index, pname, param, param_offset);
	}

	@Override
	public void glGetVertexArrayIndexed64iv(final int vaobj, final int index, final int pname, final LongBuffer param) {
		super.glGetVertexArrayIndexed64iv(vaobj, index, pname, param);
	}

	@Override
	public void glGetVertexArrayIndexed64iv(final int vaobj, final int index, final int pname, final long[] param,
			final int param_offset) {
		super.glGetVertexArrayIndexed64iv(vaobj, index, pname, param, param_offset);
	}

	@Override
	public void glCreateSamplers(final int n, final IntBuffer samplers) {
		super.glCreateSamplers(n, samplers);
	}

	@Override
	public void glCreateSamplers(final int n, final int[] samplers, final int samplers_offset) {
		super.glCreateSamplers(n, samplers, samplers_offset);
	}

	@Override
	public void glCreateProgramPipelines(final int n, final IntBuffer pipelines) {
		super.glCreateProgramPipelines(n, pipelines);
	}

	@Override
	public void glCreateProgramPipelines(final int n, final int[] pipelines, final int pipelines_offset) {
		super.glCreateProgramPipelines(n, pipelines, pipelines_offset);
	}

	@Override
	public void glCreateQueries(final int target, final int n, final IntBuffer ids) {
		super.glCreateQueries(target, n, ids);
	}

	@Override
	public void glCreateQueries(final int target, final int n, final int[] ids, final int ids_offset) {
		super.glCreateQueries(target, n, ids, ids_offset);
	}

	@Override
	public void glGetQueryBufferObjecti64v(final int id, final int buffer, final int pname, final long offset) {
		super.glGetQueryBufferObjecti64v(id, buffer, pname, offset);
	}

	@Override
	public void glGetQueryBufferObjectiv(final int id, final int buffer, final int pname, final long offset) {
		super.glGetQueryBufferObjectiv(id, buffer, pname, offset);
	}

	@Override
	public void glGetQueryBufferObjectui64v(final int id, final int buffer, final int pname, final long offset) {
		super.glGetQueryBufferObjectui64v(id, buffer, pname, offset);
	}

	@Override
	public void glGetQueryBufferObjectuiv(final int id, final int buffer, final int pname, final long offset) {
		super.glGetQueryBufferObjectuiv(id, buffer, pname, offset);
	}

	@Override
	public void glMemoryBarrierByRegion(final int barriers) {
		super.glMemoryBarrierByRegion(barriers);
	}

	@Override
	public void glGetTextureSubImage(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int format, final int type,
			final int bufSize, final Buffer pixels) {
		super.glGetTextureSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type,
				bufSize, pixels);
	}

	@Override
	public void glGetCompressedTextureSubImage(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final int bufSize,
			final Buffer pixels) {
		super.glGetCompressedTextureSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, bufSize,
				pixels);
	}

	@Override
	public int glGetGraphicsResetStatus() {
		return super.glGetGraphicsResetStatus();
	}

	@Override
	public void glGetnCompressedTexImage(final int target, final int lod, final int bufSize, final Buffer pixels) {
		super.glGetnCompressedTexImage(target, lod, bufSize, pixels);
	}

	@Override
	public void glGetnTexImage(final int target, final int level, final int format, final int type, final int bufSize,
			final Buffer pixels) {
		super.glGetnTexImage(target, level, format, type, bufSize, pixels);
	}

	@Override
	public void glGetnUniformdv(final int program, final int location, final int bufSize, final DoubleBuffer params) {
		super.glGetnUniformdv(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformdv(final int program, final int location, final int bufSize, final double[] params,
			final int params_offset) {
		super.glGetnUniformdv(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glGetnUniformfv(final int program, final int location, final int bufSize, final FloatBuffer params) {
		super.glGetnUniformfv(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformfv(final int program, final int location, final int bufSize, final float[] params,
			final int params_offset) {
		super.glGetnUniformfv(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glGetnUniformiv(final int program, final int location, final int bufSize, final IntBuffer params) {
		super.glGetnUniformiv(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformiv(final int program, final int location, final int bufSize, final int[] params,
			final int params_offset) {
		super.glGetnUniformiv(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glGetnUniformuiv(final int program, final int location, final int bufSize, final IntBuffer params) {
		super.glGetnUniformuiv(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformuiv(final int program, final int location, final int bufSize, final int[] params,
			final int params_offset) {
		super.glGetnUniformuiv(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glReadnPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final int bufSize, final Buffer data) {
		super.glReadnPixels(x, y, width, height, format, type, bufSize, data);
	}

	@Override
	public void glGetnMapdv(final int target, final int query, final int bufSize, final DoubleBuffer v) {
		super.glGetnMapdv(target, query, bufSize, v);
	}

	@Override
	public void glGetnMapdv(final int target, final int query, final int bufSize, final double[] v,
			final int v_offset) {
		super.glGetnMapdv(target, query, bufSize, v, v_offset);
	}

	@Override
	public void glGetnMapfv(final int target, final int query, final int bufSize, final FloatBuffer v) {
		super.glGetnMapfv(target, query, bufSize, v);
	}

	@Override
	public void glGetnMapfv(final int target, final int query, final int bufSize, final float[] v, final int v_offset) {
		super.glGetnMapfv(target, query, bufSize, v, v_offset);
	}

	@Override
	public void glGetnMapiv(final int target, final int query, final int bufSize, final IntBuffer v) {
		super.glGetnMapiv(target, query, bufSize, v);
	}

	@Override
	public void glGetnMapiv(final int target, final int query, final int bufSize, final int[] v, final int v_offset) {
		super.glGetnMapiv(target, query, bufSize, v, v_offset);
	}

	@Override
	public void glGetnPixelMapfv(final int map, final int bufSize, final FloatBuffer values) {
		super.glGetnPixelMapfv(map, bufSize, values);
	}

	@Override
	public void glGetnPixelMapfv(final int map, final int bufSize, final float[] values, final int values_offset) {
		super.glGetnPixelMapfv(map, bufSize, values, values_offset);
	}

	@Override
	public void glGetnPixelMapuiv(final int map, final int bufSize, final IntBuffer values) {
		super.glGetnPixelMapuiv(map, bufSize, values);
	}

	@Override
	public void glGetnPixelMapuiv(final int map, final int bufSize, final int[] values, final int values_offset) {
		super.glGetnPixelMapuiv(map, bufSize, values, values_offset);
	}

	@Override
	public void glGetnPixelMapusv(final int map, final int bufSize, final ShortBuffer values) {
		super.glGetnPixelMapusv(map, bufSize, values);
	}

	@Override
	public void glGetnPixelMapusv(final int map, final int bufSize, final short[] values, final int values_offset) {
		super.glGetnPixelMapusv(map, bufSize, values, values_offset);
	}

	@Override
	public void glGetnPolygonStipple(final int bufSize, final ByteBuffer pattern) {
		super.glGetnPolygonStipple(bufSize, pattern);
	}

	@Override
	public void glGetnPolygonStipple(final int bufSize, final byte[] pattern, final int pattern_offset) {
		super.glGetnPolygonStipple(bufSize, pattern, pattern_offset);
	}

	@Override
	public void glGetnColorTable(final int target, final int format, final int type, final int bufSize,
			final Buffer table) {
		super.glGetnColorTable(target, format, type, bufSize, table);
	}

	@Override
	public void glGetnConvolutionFilter(final int target, final int format, final int type, final int bufSize,
			final Buffer image) {
		super.glGetnConvolutionFilter(target, format, type, bufSize, image);
	}

	@Override
	public void glGetnSeparableFilter(final int target, final int format, final int type, final int rowBufSize,
			final Buffer row, final int columnBufSize, final Buffer column, final Buffer span) {
		super.glGetnSeparableFilter(target, format, type, rowBufSize, row, columnBufSize, column, span);
	}

	@Override
	public void glGetnHistogram(final int target, final boolean reset, final int format, final int type,
			final int bufSize, final Buffer values) {
		super.glGetnHistogram(target, reset, format, type, bufSize, values);
	}

	@Override
	public void glGetnMinmax(final int target, final boolean reset, final int format, final int type, final int bufSize,
			final Buffer values) {
		super.glGetnMinmax(target, reset, format, type, bufSize, values);
	}

	@Override
	public void glTextureBarrier() {
		super.glTextureBarrier();
	}

	@Override
	public void glPrimitiveBoundingBox(final float minX, final float minY, final float minZ, final float minW,
			final float maxX, final float maxY, final float maxZ, final float maxW) {
		super.glPrimitiveBoundingBox(minX, minY, minZ, minW, maxX, maxY, maxZ, maxW);
	}

	@Override
	public long glGetTextureHandleARB(final int texture) {
		return super.glGetTextureHandleARB(texture);
	}

	@Override
	public long glGetTextureSamplerHandleARB(final int texture, final int sampler) {
		return super.glGetTextureSamplerHandleARB(texture, sampler);
	}

	@Override
	public void glMakeTextureHandleResidentARB(final long handle) {
		super.glMakeTextureHandleResidentARB(handle);
	}

	@Override
	public void glMakeTextureHandleNonResidentARB(final long handle) {
		super.glMakeTextureHandleNonResidentARB(handle);
	}

	@Override
	public long glGetImageHandleARB(final int texture, final int level, final boolean layered, final int layer,
			final int format) {
		return super.glGetImageHandleARB(texture, level, layered, layer, format);
	}

	@Override
	public void glMakeImageHandleResidentARB(final long handle, final int access) {
		super.glMakeImageHandleResidentARB(handle, access);
	}

	@Override
	public void glMakeImageHandleNonResidentARB(final long handle) {
		super.glMakeImageHandleNonResidentARB(handle);
	}

	@Override
	public void glUniformHandleui64ARB(final int location, final long value) {
		super.glUniformHandleui64ARB(location, value);
	}

	@Override
	public void glUniformHandleui64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniformHandleui64vARB(location, count, value);
	}

	@Override
	public void glUniformHandleui64vARB(final int location, final int count, final long[] value,
			final int value_offset) {
		super.glUniformHandleui64vARB(location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformHandleui64ARB(final int program, final int location, final long value) {
		super.glProgramUniformHandleui64ARB(program, location, value);
	}

	@Override
	public void glProgramUniformHandleui64vARB(final int program, final int location, final int count,
			final LongBuffer values) {
		super.glProgramUniformHandleui64vARB(program, location, count, values);
	}

	@Override
	public void glProgramUniformHandleui64vARB(final int program, final int location, final int count,
			final long[] values, final int values_offset) {
		super.glProgramUniformHandleui64vARB(program, location, count, values, values_offset);
	}

	@Override
	public boolean glIsTextureHandleResidentARB(final long handle) {
		return super.glIsTextureHandleResidentARB(handle);
	}

	@Override
	public boolean glIsImageHandleResidentARB(final long handle) {
		return super.glIsImageHandleResidentARB(handle);
	}

	@Override
	public void glVertexAttribL1ui64ARB(final int index, final long x) {
		super.glVertexAttribL1ui64ARB(index, x);
	}

	@Override
	public void glVertexAttribL1ui64vARB(final int index, final LongBuffer v) {
		super.glVertexAttribL1ui64vARB(index, v);
	}

	@Override
	public void glVertexAttribL1ui64vARB(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL1ui64vARB(index, v, v_offset);
	}

	@Override
	public void glGetVertexAttribLui64vARB(final int index, final int pname, final LongBuffer params) {
		super.glGetVertexAttribLui64vARB(index, pname, params);
	}

	@Override
	public void glGetVertexAttribLui64vARB(final int index, final int pname, final long[] params,
			final int params_offset) {
		super.glGetVertexAttribLui64vARB(index, pname, params, params_offset);
	}

	@Override
	public long glCreateSyncFromCLeventARB(final long context, final long event, final int flags) {
		return super.glCreateSyncFromCLeventARB(context, event, flags);
	}

	@Override
	public void glDispatchComputeGroupSizeARB(final int num_groups_x, final int num_groups_y, final int num_groups_z,
			final int group_size_x, final int group_size_y, final int group_size_z) {
		super.glDispatchComputeGroupSizeARB(num_groups_x, num_groups_y, num_groups_z, group_size_x, group_size_y,
				group_size_z);
	}

	@Override
	public void glProgramStringARB(final int target, final int format, final int len, final String string) {
		super.glProgramStringARB(target, format, len, string);
	}

	@Override
	public void glBindProgramARB(final int target, final int program) {
		super.glBindProgramARB(target, program);
	}

	@Override
	public void glDeleteProgramsARB(final int n, final IntBuffer programs) {
		super.glDeleteProgramsARB(n, programs);
	}

	@Override
	public void glDeleteProgramsARB(final int n, final int[] programs, final int programs_offset) {
		super.glDeleteProgramsARB(n, programs, programs_offset);
	}

	@Override
	public void glGenProgramsARB(final int n, final IntBuffer programs) {
		super.glGenProgramsARB(n, programs);
	}

	@Override
	public void glGenProgramsARB(final int n, final int[] programs, final int programs_offset) {
		super.glGenProgramsARB(n, programs, programs_offset);
	}

	@Override
	public void glProgramEnvParameter4dARB(final int target, final int index, final double x, final double y,
			final double z, final double w) {
		super.glProgramEnvParameter4dARB(target, index, x, y, z, w);
	}

	@Override
	public void glProgramEnvParameter4dvARB(final int target, final int index, final DoubleBuffer params) {
		super.glProgramEnvParameter4dvARB(target, index, params);
	}

	@Override
	public void glProgramEnvParameter4dvARB(final int target, final int index, final double[] params,
			final int params_offset) {
		super.glProgramEnvParameter4dvARB(target, index, params, params_offset);
	}

	@Override
	public void glProgramEnvParameter4fARB(final int target, final int index, final float x, final float y,
			final float z, final float w) {
		super.glProgramEnvParameter4fARB(target, index, x, y, z, w);
	}

	@Override
	public void glProgramEnvParameter4fvARB(final int target, final int index, final FloatBuffer params) {
		super.glProgramEnvParameter4fvARB(target, index, params);
	}

	@Override
	public void glProgramEnvParameter4fvARB(final int target, final int index, final float[] params,
			final int params_offset) {
		super.glProgramEnvParameter4fvARB(target, index, params, params_offset);
	}

	@Override
	public void glProgramLocalParameter4dARB(final int target, final int index, final double x, final double y,
			final double z, final double w) {
		super.glProgramLocalParameter4dARB(target, index, x, y, z, w);
	}

	@Override
	public void glProgramLocalParameter4dvARB(final int target, final int index, final DoubleBuffer params) {
		super.glProgramLocalParameter4dvARB(target, index, params);
	}

	@Override
	public void glProgramLocalParameter4dvARB(final int target, final int index, final double[] params,
			final int params_offset) {
		super.glProgramLocalParameter4dvARB(target, index, params, params_offset);
	}

	@Override
	public void glProgramLocalParameter4fARB(final int target, final int index, final float x, final float y,
			final float z, final float w) {
		super.glProgramLocalParameter4fARB(target, index, x, y, z, w);
	}

	@Override
	public void glProgramLocalParameter4fvARB(final int target, final int index, final FloatBuffer params) {
		super.glProgramLocalParameter4fvARB(target, index, params);
	}

	@Override
	public void glProgramLocalParameter4fvARB(final int target, final int index, final float[] params,
			final int params_offset) {
		super.glProgramLocalParameter4fvARB(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramEnvParameterdvARB(final int target, final int index, final DoubleBuffer params) {
		super.glGetProgramEnvParameterdvARB(target, index, params);
	}

	@Override
	public void glGetProgramEnvParameterdvARB(final int target, final int index, final double[] params,
			final int params_offset) {
		super.glGetProgramEnvParameterdvARB(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramEnvParameterfvARB(final int target, final int index, final FloatBuffer params) {
		super.glGetProgramEnvParameterfvARB(target, index, params);
	}

	@Override
	public void glGetProgramEnvParameterfvARB(final int target, final int index, final float[] params,
			final int params_offset) {
		super.glGetProgramEnvParameterfvARB(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramLocalParameterdvARB(final int target, final int index, final DoubleBuffer params) {
		super.glGetProgramLocalParameterdvARB(target, index, params);
	}

	@Override
	public void glGetProgramLocalParameterdvARB(final int target, final int index, final double[] params,
			final int params_offset) {
		super.glGetProgramLocalParameterdvARB(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramLocalParameterfvARB(final int target, final int index, final FloatBuffer params) {
		super.glGetProgramLocalParameterfvARB(target, index, params);
	}

	@Override
	public void glGetProgramLocalParameterfvARB(final int target, final int index, final float[] params,
			final int params_offset) {
		super.glGetProgramLocalParameterfvARB(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramivARB(final int target, final int pname, final IntBuffer params) {
		super.glGetProgramivARB(target, pname, params);
	}

	@Override
	public void glGetProgramivARB(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetProgramivARB(target, pname, params, params_offset);
	}

	@Override
	public void glGetProgramStringARB(final int target, final int pname, final Buffer string) {
		super.glGetProgramStringARB(target, pname, string);
	}

	@Override
	public boolean glIsProgramARB(final int program) {
		return super.glIsProgramARB(program);
	}

	@Override
	public void glProgramParameteriARB(final int program, final int pname, final int value) {
		super.glProgramParameteriARB(program, pname, value);
	}

	@Override
	public void glFramebufferTextureARB(final int target, final int attachment, final int texture, final int level) {
		super.glFramebufferTextureARB(target, attachment, texture, level);
	}

	@Override
	public void glFramebufferTextureLayerARB(final int target, final int attachment, final int texture, final int level,
			final int layer) {
		super.glFramebufferTextureLayerARB(target, attachment, texture, level, layer);
	}

	@Override
	public void glFramebufferTextureFaceARB(final int target, final int attachment, final int texture, final int level,
			final int face) {
		super.glFramebufferTextureFaceARB(target, attachment, texture, level, face);
	}

	@Override
	public void glUniform1i64ARB(final int location, final long x) {
		super.glUniform1i64ARB(location, x);
	}

	@Override
	public void glUniform2i64ARB(final int location, final long x, final long y) {
		super.glUniform2i64ARB(location, x, y);
	}

	@Override
	public void glUniform3i64ARB(final int location, final long x, final long y, final long z) {
		super.glUniform3i64ARB(location, x, y, z);
	}

	@Override
	public void glUniform4i64ARB(final int location, final long x, final long y, final long z, final long w) {
		super.glUniform4i64ARB(location, x, y, z, w);
	}

	@Override
	public void glUniform1i64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform1i64vARB(location, count, value);
	}

	@Override
	public void glUniform1i64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform1i64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform2i64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform2i64vARB(location, count, value);
	}

	@Override
	public void glUniform2i64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform2i64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform3i64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform3i64vARB(location, count, value);
	}

	@Override
	public void glUniform3i64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform3i64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform4i64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform4i64vARB(location, count, value);
	}

	@Override
	public void glUniform4i64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform4i64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform1ui64ARB(final int location, final long x) {
		super.glUniform1ui64ARB(location, x);
	}

	@Override
	public void glUniform2ui64ARB(final int location, final long x, final long y) {
		super.glUniform2ui64ARB(location, x, y);
	}

	@Override
	public void glUniform3ui64ARB(final int location, final long x, final long y, final long z) {
		super.glUniform3ui64ARB(location, x, y, z);
	}

	@Override
	public void glUniform4ui64ARB(final int location, final long x, final long y, final long z, final long w) {
		super.glUniform4ui64ARB(location, x, y, z, w);
	}

	@Override
	public void glUniform1ui64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform1ui64vARB(location, count, value);
	}

	@Override
	public void glUniform1ui64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform1ui64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform2ui64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform2ui64vARB(location, count, value);
	}

	@Override
	public void glUniform2ui64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform2ui64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform3ui64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform3ui64vARB(location, count, value);
	}

	@Override
	public void glUniform3ui64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform3ui64vARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform4ui64vARB(final int location, final int count, final LongBuffer value) {
		super.glUniform4ui64vARB(location, count, value);
	}

	@Override
	public void glUniform4ui64vARB(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform4ui64vARB(location, count, value, value_offset);
	}

	@Override
	public void glGetUniformi64vARB(final int program, final int location, final LongBuffer params) {
		super.glGetUniformi64vARB(program, location, params);
	}

	@Override
	public void glGetUniformi64vARB(final int program, final int location, final long[] params,
			final int params_offset) {
		super.glGetUniformi64vARB(program, location, params, params_offset);
	}

	@Override
	public void glGetUniformui64vARB(final int program, final int location, final LongBuffer params) {
		super.glGetUniformui64vARB(program, location, params);
	}

	@Override
	public void glGetUniformui64vARB(final int program, final int location, final long[] params,
			final int params_offset) {
		super.glGetUniformui64vARB(program, location, params, params_offset);
	}

	@Override
	public void glGetnUniformi64vARB(final int program, final int location, final int bufSize,
			final LongBuffer params) {
		super.glGetnUniformi64vARB(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformi64vARB(final int program, final int location, final int bufSize, final long[] params,
			final int params_offset) {
		super.glGetnUniformi64vARB(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glGetnUniformui64vARB(final int program, final int location, final int bufSize,
			final LongBuffer params) {
		super.glGetnUniformui64vARB(program, location, bufSize, params);
	}

	@Override
	public void glGetnUniformui64vARB(final int program, final int location, final int bufSize, final long[] params,
			final int params_offset) {
		super.glGetnUniformui64vARB(program, location, bufSize, params, params_offset);
	}

	@Override
	public void glProgramUniform1i64ARB(final int program, final int location, final long x) {
		super.glProgramUniform1i64ARB(program, location, x);
	}

	@Override
	public void glProgramUniform2i64ARB(final int program, final int location, final long x, final long y) {
		super.glProgramUniform2i64ARB(program, location, x, y);
	}

	@Override
	public void glProgramUniform3i64ARB(final int program, final int location, final long x, final long y,
			final long z) {
		super.glProgramUniform3i64ARB(program, location, x, y, z);
	}

	@Override
	public void glProgramUniform4i64ARB(final int program, final int location, final long x, final long y, final long z,
			final long w) {
		super.glProgramUniform4i64ARB(program, location, x, y, z, w);
	}

	@Override
	public void glProgramUniform1i64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform1i64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform1i64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform1i64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2i64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform2i64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform2i64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform2i64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3i64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform3i64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform3i64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform3i64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4i64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform4i64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform4i64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform4i64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform1ui64ARB(final int program, final int location, final long x) {
		super.glProgramUniform1ui64ARB(program, location, x);
	}

	@Override
	public void glProgramUniform2ui64ARB(final int program, final int location, final long x, final long y) {
		super.glProgramUniform2ui64ARB(program, location, x, y);
	}

	@Override
	public void glProgramUniform3ui64ARB(final int program, final int location, final long x, final long y,
			final long z) {
		super.glProgramUniform3ui64ARB(program, location, x, y, z);
	}

	@Override
	public void glProgramUniform4ui64ARB(final int program, final int location, final long x, final long y,
			final long z, final long w) {
		super.glProgramUniform4ui64ARB(program, location, x, y, z, w);
	}

	@Override
	public void glProgramUniform1ui64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform1ui64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform1ui64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform1ui64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2ui64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform2ui64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform2ui64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform2ui64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3ui64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform3ui64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform3ui64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform3ui64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4ui64vARB(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform4ui64vARB(program, location, count, value);
	}

	@Override
	public void glProgramUniform4ui64vARB(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform4ui64vARB(program, location, count, value, value_offset);
	}

	@Override
	public void glColorTable(final int target, final int internalformat, final int width, final int format,
			final int type, final Buffer table) {
		super.glColorTable(target, internalformat, width, format, type, table);
	}

	@Override
	public void glColorTable(final int target, final int internalformat, final int width, final int format,
			final int type, final long table_buffer_offset) {
		super.glColorTable(target, internalformat, width, format, type, table_buffer_offset);
	}

	@Override
	public void glColorTableParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glColorTableParameterfv(target, pname, params);
	}

	@Override
	public void glColorTableParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glColorTableParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glColorTableParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glColorTableParameteriv(target, pname, params);
	}

	@Override
	public void glColorTableParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glColorTableParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glCopyColorTable(final int target, final int internalformat, final int x, final int y,
			final int width) {
		super.glCopyColorTable(target, internalformat, x, y, width);
	}

	@Override
	public void glGetColorTable(final int target, final int format, final int type, final Buffer table) {
		super.glGetColorTable(target, format, type, table);
	}

	@Override
	public void glGetColorTable(final int target, final int format, final int type, final long table_buffer_offset) {
		super.glGetColorTable(target, format, type, table_buffer_offset);
	}

	@Override
	public void glGetColorTableParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glGetColorTableParameterfv(target, pname, params);
	}

	@Override
	public void glGetColorTableParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetColorTableParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glGetColorTableParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetColorTableParameteriv(target, pname, params);
	}

	@Override
	public void glGetColorTableParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetColorTableParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glColorSubTable(final int target, final int start, final int count, final int format, final int type,
			final Buffer data) {
		super.glColorSubTable(target, start, count, format, type, data);
	}

	@Override
	public void glColorSubTable(final int target, final int start, final int count, final int format, final int type,
			final long data_buffer_offset) {
		super.glColorSubTable(target, start, count, format, type, data_buffer_offset);
	}

	@Override
	public void glCopyColorSubTable(final int target, final int start, final int x, final int y, final int width) {
		super.glCopyColorSubTable(target, start, x, y, width);
	}

	@Override
	public void glConvolutionFilter1D(final int target, final int internalformat, final int width, final int format,
			final int type, final Buffer image) {
		super.glConvolutionFilter1D(target, internalformat, width, format, type, image);
	}

	@Override
	public void glConvolutionFilter1D(final int target, final int internalformat, final int width, final int format,
			final int type, final long image_buffer_offset) {
		super.glConvolutionFilter1D(target, internalformat, width, format, type, image_buffer_offset);
	}

	@Override
	public void glConvolutionFilter2D(final int target, final int internalformat, final int width, final int height,
			final int format, final int type, final Buffer image) {
		super.glConvolutionFilter2D(target, internalformat, width, height, format, type, image);
	}

	@Override
	public void glConvolutionFilter2D(final int target, final int internalformat, final int width, final int height,
			final int format, final int type, final long image_buffer_offset) {
		super.glConvolutionFilter2D(target, internalformat, width, height, format, type, image_buffer_offset);
	}

	@Override
	public void glConvolutionParameterf(final int target, final int pname, final float params) {
		super.glConvolutionParameterf(target, pname, params);
	}

	@Override
	public void glConvolutionParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glConvolutionParameterfv(target, pname, params);
	}

	@Override
	public void glConvolutionParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glConvolutionParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glConvolutionParameteri(final int target, final int pname, final int params) {
		super.glConvolutionParameteri(target, pname, params);
	}

	@Override
	public void glConvolutionParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glConvolutionParameteriv(target, pname, params);
	}

	@Override
	public void glConvolutionParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glConvolutionParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glCopyConvolutionFilter1D(final int target, final int internalformat, final int x, final int y,
			final int width) {
		super.glCopyConvolutionFilter1D(target, internalformat, x, y, width);
	}

	@Override
	public void glCopyConvolutionFilter2D(final int target, final int internalformat, final int x, final int y,
			final int width, final int height) {
		super.glCopyConvolutionFilter2D(target, internalformat, x, y, width, height);
	}

	@Override
	public void glGetConvolutionFilter(final int target, final int format, final int type, final Buffer image) {
		super.glGetConvolutionFilter(target, format, type, image);
	}

	@Override
	public void glGetConvolutionFilter(final int target, final int format, final int type,
			final long image_buffer_offset) {
		super.glGetConvolutionFilter(target, format, type, image_buffer_offset);
	}

	@Override
	public void glGetConvolutionParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glGetConvolutionParameterfv(target, pname, params);
	}

	@Override
	public void glGetConvolutionParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetConvolutionParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glGetConvolutionParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetConvolutionParameteriv(target, pname, params);
	}

	@Override
	public void glGetConvolutionParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetConvolutionParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glGetSeparableFilter(final int target, final int format, final int type, final Buffer row,
			final Buffer column, final Buffer span) {
		super.glGetSeparableFilter(target, format, type, row, column, span);
	}

	@Override
	public void glGetSeparableFilter(final int target, final int format, final int type, final long row_buffer_offset,
			final long column_buffer_offset, final long span_buffer_offset) {
		super.glGetSeparableFilter(target, format, type, row_buffer_offset, column_buffer_offset, span_buffer_offset);
	}

	@Override
	public void glSeparableFilter2D(final int target, final int internalformat, final int width, final int height,
			final int format, final int type, final Buffer row, final Buffer column) {
		super.glSeparableFilter2D(target, internalformat, width, height, format, type, row, column);
	}

	@Override
	public void glSeparableFilter2D(final int target, final int internalformat, final int width, final int height,
			final int format, final int type, final long row_buffer_offset, final long column_buffer_offset) {
		super.glSeparableFilter2D(target, internalformat, width, height, format, type, row_buffer_offset,
				column_buffer_offset);
	}

	@Override
	public void glGetHistogram(final int target, final boolean reset, final int format, final int type,
			final Buffer values) {
		super.glGetHistogram(target, reset, format, type, values);
	}

	@Override
	public void glGetHistogram(final int target, final boolean reset, final int format, final int type,
			final long values_buffer_offset) {
		super.glGetHistogram(target, reset, format, type, values_buffer_offset);
	}

	@Override
	public void glGetHistogramParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glGetHistogramParameterfv(target, pname, params);
	}

	@Override
	public void glGetHistogramParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetHistogramParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glGetHistogramParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetHistogramParameteriv(target, pname, params);
	}

	@Override
	public void glGetHistogramParameteriv(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetHistogramParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glGetMinmax(final int target, final boolean reset, final int format, final int type,
			final Buffer values) {
		super.glGetMinmax(target, reset, format, type, values);
	}

	@Override
	public void glGetMinmax(final int target, final boolean reset, final int format, final int type,
			final long values_buffer_offset) {
		super.glGetMinmax(target, reset, format, type, values_buffer_offset);
	}

	@Override
	public void glGetMinmaxParameterfv(final int target, final int pname, final FloatBuffer params) {
		super.glGetMinmaxParameterfv(target, pname, params);
	}

	@Override
	public void glGetMinmaxParameterfv(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMinmaxParameterfv(target, pname, params, params_offset);
	}

	@Override
	public void glGetMinmaxParameteriv(final int target, final int pname, final IntBuffer params) {
		super.glGetMinmaxParameteriv(target, pname, params);
	}

	@Override
	public void glGetMinmaxParameteriv(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetMinmaxParameteriv(target, pname, params, params_offset);
	}

	@Override
	public void glHistogram(final int target, final int width, final int internalformat, final boolean sink) {
		super.glHistogram(target, width, internalformat, sink);
	}

	@Override
	public void glMinmax(final int target, final int internalformat, final boolean sink) {
		super.glMinmax(target, internalformat, sink);
	}

	@Override
	public void glResetHistogram(final int target) {
		super.glResetHistogram(target);
	}

	@Override
	public void glResetMinmax(final int target) {
		super.glResetMinmax(target);
	}

	@Override
	public void glMultiDrawArraysIndirectCountARB(final int mode, final long indirect, final long drawcount,
			final int maxdrawcount, final int stride) {
		super.glMultiDrawArraysIndirectCountARB(mode, indirect, drawcount, maxdrawcount, stride);
	}

	@Override
	public void glMultiDrawElementsIndirectCountARB(final int mode, final int type, final long indirect,
			final long drawcount, final int maxdrawcount, final int stride) {
		super.glMultiDrawElementsIndirectCountARB(mode, type, indirect, drawcount, maxdrawcount, stride);
	}

	@Override
	public void glCurrentPaletteMatrixARB(final int index) {
		super.glCurrentPaletteMatrixARB(index);
	}

	@Override
	public void glMatrixIndexubvARB(final int size, final ByteBuffer indices) {
		super.glMatrixIndexubvARB(size, indices);
	}

	@Override
	public void glMatrixIndexubvARB(final int size, final byte[] indices, final int indices_offset) {
		super.glMatrixIndexubvARB(size, indices, indices_offset);
	}

	@Override
	public void glMatrixIndexusvARB(final int size, final ShortBuffer indices) {
		super.glMatrixIndexusvARB(size, indices);
	}

	@Override
	public void glMatrixIndexusvARB(final int size, final short[] indices, final int indices_offset) {
		super.glMatrixIndexusvARB(size, indices, indices_offset);
	}

	@Override
	public void glMatrixIndexuivARB(final int size, final IntBuffer indices) {
		super.glMatrixIndexuivARB(size, indices);
	}

	@Override
	public void glMatrixIndexuivARB(final int size, final int[] indices, final int indices_offset) {
		super.glMatrixIndexuivARB(size, indices, indices_offset);
	}

	@Override
	public void glMatrixIndexPointerARB(final int size, final int type, final int stride, final Buffer pointer) {
		super.glMatrixIndexPointerARB(size, type, stride, pointer);
	}

	@Override
	public void glMatrixIndexPointerARB(final int size, final int type, final int stride,
			final long pointer_buffer_offset) {
		super.glMatrixIndexPointerARB(size, type, stride, pointer_buffer_offset);
	}

	@Override
	public void glMaxShaderCompilerThreadsARB(final int count) {
		super.glMaxShaderCompilerThreadsARB(count);
	}

	@Override
	public void glFramebufferSampleLocationsfvARB(final int target, final int start, final int count,
			final FloatBuffer v) {
		super.glFramebufferSampleLocationsfvARB(target, start, count, v);
	}

	@Override
	public void glFramebufferSampleLocationsfvARB(final int target, final int start, final int count, final float[] v,
			final int v_offset) {
		super.glFramebufferSampleLocationsfvARB(target, start, count, v, v_offset);
	}

	@Override
	public void glNamedFramebufferSampleLocationsfvARB(final int framebuffer, final int start, final int count,
			final FloatBuffer v) {
		super.glNamedFramebufferSampleLocationsfvARB(framebuffer, start, count, v);
	}

	@Override
	public void glNamedFramebufferSampleLocationsfvARB(final int framebuffer, final int start, final int count,
			final float[] v, final int v_offset) {
		super.glNamedFramebufferSampleLocationsfvARB(framebuffer, start, count, v, v_offset);
	}

	@Override
	public void glEvaluateDepthValuesARB() {
		super.glEvaluateDepthValuesARB();
	}

	@Override
	public void glDeleteObjectARB(final long obj) {
		super.glDeleteObjectARB(obj);
	}

	@Override
	public long glGetHandleARB(final int pname) {
		return super.glGetHandleARB(pname);
	}

	@Override
	public void glDetachObjectARB(final long containerObj, final long attachedObj) {
		super.glDetachObjectARB(containerObj, attachedObj);
	}

	@Override
	public long glCreateShaderObjectARB(final int shaderType) {
		return super.glCreateShaderObjectARB(shaderType);
	}

	@Override
	public void glShaderSourceARB(final long shaderObj, final int count, final String[] string,
			final IntBuffer length) {
		super.glShaderSourceARB(shaderObj, count, string, length);
	}

	@Override
	public void glShaderSourceARB(final long shaderObj, final int count, final String[] string, final int[] length,
			final int length_offset) {
		super.glShaderSourceARB(shaderObj, count, string, length, length_offset);
	}

	@Override
	public void glCompileShaderARB(final long shaderObj) {
		super.glCompileShaderARB(shaderObj);
	}

	@Override
	public long glCreateProgramObjectARB() {
		return super.glCreateProgramObjectARB();
	}

	@Override
	public void glAttachObjectARB(final long containerObj, final long obj) {
		super.glAttachObjectARB(containerObj, obj);
	}

	@Override
	public void glLinkProgramARB(final long programObj) {
		super.glLinkProgramARB(programObj);
	}

	@Override
	public void glUseProgramObjectARB(final long programObj) {
		super.glUseProgramObjectARB(programObj);
	}

	@Override
	public void glValidateProgramARB(final long programObj) {
		super.glValidateProgramARB(programObj);
	}

	@Override
	public void glUniform1fARB(final int location, final float v0) {
		super.glUniform1fARB(location, v0);
	}

	@Override
	public void glUniform2fARB(final int location, final float v0, final float v1) {
		super.glUniform2fARB(location, v0, v1);
	}

	@Override
	public void glUniform3fARB(final int location, final float v0, final float v1, final float v2) {
		super.glUniform3fARB(location, v0, v1, v2);
	}

	@Override
	public void glUniform4fARB(final int location, final float v0, final float v1, final float v2, final float v3) {
		super.glUniform4fARB(location, v0, v1, v2, v3);
	}

	@Override
	public void glUniform1iARB(final int location, final int v0) {
		super.glUniform1iARB(location, v0);
	}

	@Override
	public void glUniform2iARB(final int location, final int v0, final int v1) {
		super.glUniform2iARB(location, v0, v1);
	}

	@Override
	public void glUniform3iARB(final int location, final int v0, final int v1, final int v2) {
		super.glUniform3iARB(location, v0, v1, v2);
	}

	@Override
	public void glUniform4iARB(final int location, final int v0, final int v1, final int v2, final int v3) {
		super.glUniform4iARB(location, v0, v1, v2, v3);
	}

	@Override
	public void glUniform1fvARB(final int location, final int count, final FloatBuffer value) {
		super.glUniform1fvARB(location, count, value);
	}

	@Override
	public void glUniform1fvARB(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform1fvARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform2fvARB(final int location, final int count, final FloatBuffer value) {
		super.glUniform2fvARB(location, count, value);
	}

	@Override
	public void glUniform2fvARB(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform2fvARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform3fvARB(final int location, final int count, final FloatBuffer value) {
		super.glUniform3fvARB(location, count, value);
	}

	@Override
	public void glUniform3fvARB(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform3fvARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform4fvARB(final int location, final int count, final FloatBuffer value) {
		super.glUniform4fvARB(location, count, value);
	}

	@Override
	public void glUniform4fvARB(final int location, final int count, final float[] value, final int value_offset) {
		super.glUniform4fvARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform1ivARB(final int location, final int count, final IntBuffer value) {
		super.glUniform1ivARB(location, count, value);
	}

	@Override
	public void glUniform1ivARB(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform1ivARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform2ivARB(final int location, final int count, final IntBuffer value) {
		super.glUniform2ivARB(location, count, value);
	}

	@Override
	public void glUniform2ivARB(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform2ivARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform3ivARB(final int location, final int count, final IntBuffer value) {
		super.glUniform3ivARB(location, count, value);
	}

	@Override
	public void glUniform3ivARB(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform3ivARB(location, count, value, value_offset);
	}

	@Override
	public void glUniform4ivARB(final int location, final int count, final IntBuffer value) {
		super.glUniform4ivARB(location, count, value);
	}

	@Override
	public void glUniform4ivARB(final int location, final int count, final int[] value, final int value_offset) {
		super.glUniform4ivARB(location, count, value, value_offset);
	}

	@Override
	public void glUniformMatrix2fvARB(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix2fvARB(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2fvARB(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix2fvARB(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix3fvARB(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix3fvARB(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3fvARB(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix3fvARB(location, count, transpose, value, value_offset);
	}

	@Override
	public void glUniformMatrix4fvARB(final int location, final int count, final boolean transpose,
			final FloatBuffer value) {
		super.glUniformMatrix4fvARB(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4fvARB(final int location, final int count, final boolean transpose, final float[] value,
			final int value_offset) {
		super.glUniformMatrix4fvARB(location, count, transpose, value, value_offset);
	}

	@Override
	public void glGetObjectParameterfvARB(final long obj, final int pname, final FloatBuffer params) {
		super.glGetObjectParameterfvARB(obj, pname, params);
	}

	@Override
	public void glGetObjectParameterfvARB(final long obj, final int pname, final float[] params,
			final int params_offset) {
		super.glGetObjectParameterfvARB(obj, pname, params, params_offset);
	}

	@Override
	public void glGetObjectParameterivARB(final long obj, final int pname, final IntBuffer params) {
		super.glGetObjectParameterivARB(obj, pname, params);
	}

	@Override
	public void glGetObjectParameterivARB(final long obj, final int pname, final int[] params,
			final int params_offset) {
		super.glGetObjectParameterivARB(obj, pname, params, params_offset);
	}

	@Override
	public void glGetInfoLogARB(final long obj, final int maxLength, final IntBuffer length, final ByteBuffer infoLog) {
		super.glGetInfoLogARB(obj, maxLength, length, infoLog);
	}

	@Override
	public void glGetInfoLogARB(final long obj, final int maxLength, final int[] length, final int length_offset,
			final byte[] infoLog, final int infoLog_offset) {
		super.glGetInfoLogARB(obj, maxLength, length, length_offset, infoLog, infoLog_offset);
	}

	@Override
	public void glGetAttachedObjectsARB(final long containerObj, final int maxCount, final IntBuffer count,
			final LongBuffer obj) {
		super.glGetAttachedObjectsARB(containerObj, maxCount, count, obj);
	}

	@Override
	public void glGetAttachedObjectsARB(final long containerObj, final int maxCount, final int[] count,
			final int count_offset, final long[] obj, final int obj_offset) {
		super.glGetAttachedObjectsARB(containerObj, maxCount, count, count_offset, obj, obj_offset);
	}

	@Override
	public int glGetUniformLocationARB(final long programObj, final String name) {
		return super.glGetUniformLocationARB(programObj, name);
	}

	@Override
	public void glGetActiveUniformARB(final long programObj, final int index, final int maxLength,
			final IntBuffer length, final IntBuffer size, final IntBuffer type, final ByteBuffer name) {
		super.glGetActiveUniformARB(programObj, index, maxLength, length, size, type, name);
	}

	@Override
	public void glGetActiveUniformARB(final long programObj, final int index, final int maxLength, final int[] length,
			final int length_offset, final int[] size, final int size_offset, final int[] type, final int type_offset,
			final byte[] name, final int name_offset) {
		super.glGetActiveUniformARB(programObj, index, maxLength, length, length_offset, size, size_offset, type,
				type_offset, name, name_offset);
	}

	@Override
	public void glGetUniformfvARB(final long programObj, final int location, final FloatBuffer params) {
		super.glGetUniformfvARB(programObj, location, params);
	}

	@Override
	public void glGetUniformfvARB(final long programObj, final int location, final float[] params,
			final int params_offset) {
		super.glGetUniformfvARB(programObj, location, params, params_offset);
	}

	@Override
	public void glGetUniformivARB(final long programObj, final int location, final IntBuffer params) {
		super.glGetUniformivARB(programObj, location, params);
	}

	@Override
	public void glGetUniformivARB(final long programObj, final int location, final int[] params,
			final int params_offset) {
		super.glGetUniformivARB(programObj, location, params, params_offset);
	}

	@Override
	public void glGetShaderSourceARB(final long obj, final int maxLength, final IntBuffer length,
			final ByteBuffer source) {
		super.glGetShaderSourceARB(obj, maxLength, length, source);
	}

	@Override
	public void glGetShaderSourceARB(final long obj, final int maxLength, final int[] length, final int length_offset,
			final byte[] source, final int source_offset) {
		super.glGetShaderSourceARB(obj, maxLength, length, length_offset, source, source_offset);
	}

	@Override
	public void glNamedStringARB(final int type, final int namelen, final String name, final int stringlen,
			final String string) {
		super.glNamedStringARB(type, namelen, name, stringlen, string);
	}

	@Override
	public void glDeleteNamedStringARB(final int namelen, final String name) {
		super.glDeleteNamedStringARB(namelen, name);
	}

	@Override
	public void glCompileShaderIncludeARB(final int shader, final int count, final String[] path,
			final IntBuffer length) {
		super.glCompileShaderIncludeARB(shader, count, path, length);
	}

	@Override
	public void glCompileShaderIncludeARB(final int shader, final int count, final String[] path, final int[] length,
			final int length_offset) {
		super.glCompileShaderIncludeARB(shader, count, path, length, length_offset);
	}

	@Override
	public boolean glIsNamedStringARB(final int namelen, final String name) {
		return super.glIsNamedStringARB(namelen, name);
	}

	@Override
	public void glGetNamedStringARB(final int namelen, final String name, final int bufSize, final IntBuffer stringlen,
			final ByteBuffer string) {
		super.glGetNamedStringARB(namelen, name, bufSize, stringlen, string);
	}

	@Override
	public void glGetNamedStringARB(final int namelen, final String name, final int bufSize, final int[] stringlen,
			final int stringlen_offset, final byte[] string, final int string_offset) {
		super.glGetNamedStringARB(namelen, name, bufSize, stringlen, stringlen_offset, string, string_offset);
	}

	@Override
	public void glGetNamedStringivARB(final int namelen, final String name, final int pname, final IntBuffer params) {
		super.glGetNamedStringivARB(namelen, name, pname, params);
	}

	@Override
	public void glGetNamedStringivARB(final int namelen, final String name, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedStringivARB(namelen, name, pname, params, params_offset);
	}

	@Override
	public void glBufferPageCommitmentARB(final int target, final long offset, final long size, final boolean commit) {
		super.glBufferPageCommitmentARB(target, offset, size, commit);
	}

	@Override
	public void glNamedBufferPageCommitmentEXT(final int buffer, final long offset, final long size,
			final boolean commit) {
		super.glNamedBufferPageCommitmentEXT(buffer, offset, size, commit);
	}

	@Override
	public void glNamedBufferPageCommitmentARB(final int buffer, final long offset, final long size,
			final boolean commit) {
		super.glNamedBufferPageCommitmentARB(buffer, offset, size, commit);
	}

	@Override
	public void glTexPageCommitmentARB(final int target, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final boolean commit) {
		super.glTexPageCommitmentARB(target, level, xoffset, yoffset, zoffset, width, height, depth, commit);
	}

	@Override
	public void glWeightbvARB(final int size, final ByteBuffer weights) {
		super.glWeightbvARB(size, weights);
	}

	@Override
	public void glWeightbvARB(final int size, final byte[] weights, final int weights_offset) {
		super.glWeightbvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightsvARB(final int size, final ShortBuffer weights) {
		super.glWeightsvARB(size, weights);
	}

	@Override
	public void glWeightsvARB(final int size, final short[] weights, final int weights_offset) {
		super.glWeightsvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightivARB(final int size, final IntBuffer weights) {
		super.glWeightivARB(size, weights);
	}

	@Override
	public void glWeightivARB(final int size, final int[] weights, final int weights_offset) {
		super.glWeightivARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightfvARB(final int size, final FloatBuffer weights) {
		super.glWeightfvARB(size, weights);
	}

	@Override
	public void glWeightfvARB(final int size, final float[] weights, final int weights_offset) {
		super.glWeightfvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightdvARB(final int size, final DoubleBuffer weights) {
		super.glWeightdvARB(size, weights);
	}

	@Override
	public void glWeightdvARB(final int size, final double[] weights, final int weights_offset) {
		super.glWeightdvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightubvARB(final int size, final ByteBuffer weights) {
		super.glWeightubvARB(size, weights);
	}

	@Override
	public void glWeightubvARB(final int size, final byte[] weights, final int weights_offset) {
		super.glWeightubvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightusvARB(final int size, final ShortBuffer weights) {
		super.glWeightusvARB(size, weights);
	}

	@Override
	public void glWeightusvARB(final int size, final short[] weights, final int weights_offset) {
		super.glWeightusvARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightuivARB(final int size, final IntBuffer weights) {
		super.glWeightuivARB(size, weights);
	}

	@Override
	public void glWeightuivARB(final int size, final int[] weights, final int weights_offset) {
		super.glWeightuivARB(size, weights, weights_offset);
	}

	@Override
	public void glWeightPointerARB(final int size, final int type, final int stride, final Buffer pointer) {
		super.glWeightPointerARB(size, type, stride, pointer);
	}

	@Override
	public void glWeightPointerARB(final int size, final int type, final int stride, final long pointer_buffer_offset) {
		super.glWeightPointerARB(size, type, stride, pointer_buffer_offset);
	}

	@Override
	public void glVertexBlendARB(final int count) {
		super.glVertexBlendARB(count);
	}

	@Override
	public void glVertexAttrib1dARB(final int index, final double x) {
		super.glVertexAttrib1dARB(index, x);
	}

	@Override
	public void glVertexAttrib1dvARB(final int index, final DoubleBuffer v) {
		super.glVertexAttrib1dvARB(index, v);
	}

	@Override
	public void glVertexAttrib1dvARB(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib1dvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib1fARB(final int index, final float x) {
		super.glVertexAttrib1fARB(index, x);
	}

	@Override
	public void glVertexAttrib1fvARB(final int index, final FloatBuffer v) {
		super.glVertexAttrib1fvARB(index, v);
	}

	@Override
	public void glVertexAttrib1fvARB(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib1fvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib1sARB(final int index, final short x) {
		super.glVertexAttrib1sARB(index, x);
	}

	@Override
	public void glVertexAttrib1svARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib1svARB(index, v);
	}

	@Override
	public void glVertexAttrib1svARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib1svARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2dARB(final int index, final double x, final double y) {
		super.glVertexAttrib2dARB(index, x, y);
	}

	@Override
	public void glVertexAttrib2dvARB(final int index, final DoubleBuffer v) {
		super.glVertexAttrib2dvARB(index, v);
	}

	@Override
	public void glVertexAttrib2dvARB(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib2dvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2fARB(final int index, final float x, final float y) {
		super.glVertexAttrib2fARB(index, x, y);
	}

	@Override
	public void glVertexAttrib2fvARB(final int index, final FloatBuffer v) {
		super.glVertexAttrib2fvARB(index, v);
	}

	@Override
	public void glVertexAttrib2fvARB(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib2fvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2sARB(final int index, final short x, final short y) {
		super.glVertexAttrib2sARB(index, x, y);
	}

	@Override
	public void glVertexAttrib2svARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib2svARB(index, v);
	}

	@Override
	public void glVertexAttrib2svARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib2svARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3dARB(final int index, final double x, final double y, final double z) {
		super.glVertexAttrib3dARB(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3dvARB(final int index, final DoubleBuffer v) {
		super.glVertexAttrib3dvARB(index, v);
	}

	@Override
	public void glVertexAttrib3dvARB(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib3dvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3fARB(final int index, final float x, final float y, final float z) {
		super.glVertexAttrib3fARB(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3fvARB(final int index, final FloatBuffer v) {
		super.glVertexAttrib3fvARB(index, v);
	}

	@Override
	public void glVertexAttrib3fvARB(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib3fvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3sARB(final int index, final short x, final short y, final short z) {
		super.glVertexAttrib3sARB(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3svARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib3svARB(index, v);
	}

	@Override
	public void glVertexAttrib3svARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib3svARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NbvARB(final int index, final ByteBuffer v) {
		super.glVertexAttrib4NbvARB(index, v);
	}

	@Override
	public void glVertexAttrib4NbvARB(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4NbvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NivARB(final int index, final IntBuffer v) {
		super.glVertexAttrib4NivARB(index, v);
	}

	@Override
	public void glVertexAttrib4NivARB(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4NivARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NsvARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib4NsvARB(index, v);
	}

	@Override
	public void glVertexAttrib4NsvARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4NsvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NubARB(final int index, final byte x, final byte y, final byte z, final byte w) {
		super.glVertexAttrib4NubARB(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4NubvARB(final int index, final ByteBuffer v) {
		super.glVertexAttrib4NubvARB(index, v);
	}

	@Override
	public void glVertexAttrib4NubvARB(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4NubvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NuivARB(final int index, final IntBuffer v) {
		super.glVertexAttrib4NuivARB(index, v);
	}

	@Override
	public void glVertexAttrib4NuivARB(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4NuivARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4NusvARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib4NusvARB(index, v);
	}

	@Override
	public void glVertexAttrib4NusvARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4NusvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4bvARB(final int index, final ByteBuffer v) {
		super.glVertexAttrib4bvARB(index, v);
	}

	@Override
	public void glVertexAttrib4bvARB(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4bvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4dARB(final int index, final double x, final double y, final double z, final double w) {
		super.glVertexAttrib4dARB(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4dvARB(final int index, final DoubleBuffer v) {
		super.glVertexAttrib4dvARB(index, v);
	}

	@Override
	public void glVertexAttrib4dvARB(final int index, final double[] v, final int v_offset) {
		super.glVertexAttrib4dvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4fARB(final int index, final float x, final float y, final float z, final float w) {
		super.glVertexAttrib4fARB(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4fvARB(final int index, final FloatBuffer v) {
		super.glVertexAttrib4fvARB(index, v);
	}

	@Override
	public void glVertexAttrib4fvARB(final int index, final float[] v, final int v_offset) {
		super.glVertexAttrib4fvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4ivARB(final int index, final IntBuffer v) {
		super.glVertexAttrib4ivARB(index, v);
	}

	@Override
	public void glVertexAttrib4ivARB(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4ivARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4sARB(final int index, final short x, final short y, final short z, final short w) {
		super.glVertexAttrib4sARB(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4svARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib4svARB(index, v);
	}

	@Override
	public void glVertexAttrib4svARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4svARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4ubvARB(final int index, final ByteBuffer v) {
		super.glVertexAttrib4ubvARB(index, v);
	}

	@Override
	public void glVertexAttrib4ubvARB(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttrib4ubvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4uivARB(final int index, final IntBuffer v) {
		super.glVertexAttrib4uivARB(index, v);
	}

	@Override
	public void glVertexAttrib4uivARB(final int index, final int[] v, final int v_offset) {
		super.glVertexAttrib4uivARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4usvARB(final int index, final ShortBuffer v) {
		super.glVertexAttrib4usvARB(index, v);
	}

	@Override
	public void glVertexAttrib4usvARB(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4usvARB(index, v, v_offset);
	}

	@Override
	public void glVertexAttribPointerARB(final int index, final int size, final int type, final boolean normalized,
			final int stride, final Buffer pointer) {
		super.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer);
	}

	@Override
	public void glVertexAttribPointerARB(final int index, final int size, final int type, final boolean normalized,
			final int stride, final long pointer_buffer_offset) {
		super.glVertexAttribPointerARB(index, size, type, normalized, stride, pointer_buffer_offset);
	}

	@Override
	public void glEnableVertexAttribArrayARB(final int index) {
		super.glEnableVertexAttribArrayARB(index);
	}

	@Override
	public void glDisableVertexAttribArrayARB(final int index) {
		super.glDisableVertexAttribArrayARB(index);
	}

	@Override
	public void glGetVertexAttribdvARB(final int index, final int pname, final DoubleBuffer params) {
		super.glGetVertexAttribdvARB(index, pname, params);
	}

	@Override
	public void glGetVertexAttribdvARB(final int index, final int pname, final double[] params,
			final int params_offset) {
		super.glGetVertexAttribdvARB(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribfvARB(final int index, final int pname, final FloatBuffer params) {
		super.glGetVertexAttribfvARB(index, pname, params);
	}

	@Override
	public void glGetVertexAttribfvARB(final int index, final int pname, final float[] params,
			final int params_offset) {
		super.glGetVertexAttribfvARB(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribivARB(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribivARB(index, pname, params);
	}

	@Override
	public void glGetVertexAttribivARB(final int index, final int pname, final int[] params, final int params_offset) {
		super.glGetVertexAttribivARB(index, pname, params, params_offset);
	}

	@Override
	public void glBlendBarrier() {
		super.glBlendBarrier();
	}

	@Override
	public void glMultiTexCoord1bOES(final int texture, final byte s) {
		super.glMultiTexCoord1bOES(texture, s);
	}

	@Override
	public void glMultiTexCoord1bvOES(final int texture, final ByteBuffer coords) {
		super.glMultiTexCoord1bvOES(texture, coords);
	}

	@Override
	public void glMultiTexCoord1bvOES(final int texture, final byte[] coords, final int coords_offset) {
		super.glMultiTexCoord1bvOES(texture, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoord2bOES(final int texture, final byte s, final byte t) {
		super.glMultiTexCoord2bOES(texture, s, t);
	}

	@Override
	public void glMultiTexCoord2bvOES(final int texture, final ByteBuffer coords) {
		super.glMultiTexCoord2bvOES(texture, coords);
	}

	@Override
	public void glMultiTexCoord2bvOES(final int texture, final byte[] coords, final int coords_offset) {
		super.glMultiTexCoord2bvOES(texture, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoord3bOES(final int texture, final byte s, final byte t, final byte r) {
		super.glMultiTexCoord3bOES(texture, s, t, r);
	}

	@Override
	public void glMultiTexCoord3bvOES(final int texture, final ByteBuffer coords) {
		super.glMultiTexCoord3bvOES(texture, coords);
	}

	@Override
	public void glMultiTexCoord3bvOES(final int texture, final byte[] coords, final int coords_offset) {
		super.glMultiTexCoord3bvOES(texture, coords, coords_offset);
	}

	@Override
	public void glMultiTexCoord4bOES(final int texture, final byte s, final byte t, final byte r, final byte q) {
		super.glMultiTexCoord4bOES(texture, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4bvOES(final int texture, final ByteBuffer coords) {
		super.glMultiTexCoord4bvOES(texture, coords);
	}

	@Override
	public void glMultiTexCoord4bvOES(final int texture, final byte[] coords, final int coords_offset) {
		super.glMultiTexCoord4bvOES(texture, coords, coords_offset);
	}

	@Override
	public void glTexCoord1bOES(final byte s) {
		super.glTexCoord1bOES(s);
	}

	@Override
	public void glTexCoord1bvOES(final ByteBuffer coords) {
		super.glTexCoord1bvOES(coords);
	}

	@Override
	public void glTexCoord1bvOES(final byte[] coords, final int coords_offset) {
		super.glTexCoord1bvOES(coords, coords_offset);
	}

	@Override
	public void glTexCoord2bOES(final byte s, final byte t) {
		super.glTexCoord2bOES(s, t);
	}

	@Override
	public void glTexCoord2bvOES(final ByteBuffer coords) {
		super.glTexCoord2bvOES(coords);
	}

	@Override
	public void glTexCoord2bvOES(final byte[] coords, final int coords_offset) {
		super.glTexCoord2bvOES(coords, coords_offset);
	}

	@Override
	public void glTexCoord3bOES(final byte s, final byte t, final byte r) {
		super.glTexCoord3bOES(s, t, r);
	}

	@Override
	public void glTexCoord3bvOES(final ByteBuffer coords) {
		super.glTexCoord3bvOES(coords);
	}

	@Override
	public void glTexCoord3bvOES(final byte[] coords, final int coords_offset) {
		super.glTexCoord3bvOES(coords, coords_offset);
	}

	@Override
	public void glTexCoord4bOES(final byte s, final byte t, final byte r, final byte q) {
		super.glTexCoord4bOES(s, t, r, q);
	}

	@Override
	public void glTexCoord4bvOES(final ByteBuffer coords) {
		super.glTexCoord4bvOES(coords);
	}

	@Override
	public void glTexCoord4bvOES(final byte[] coords, final int coords_offset) {
		super.glTexCoord4bvOES(coords, coords_offset);
	}

	@Override
	public void glVertex2bOES(final byte x, final byte y) {
		super.glVertex2bOES(x, y);
	}

	@Override
	public void glVertex2bvOES(final ByteBuffer coords) {
		super.glVertex2bvOES(coords);
	}

	@Override
	public void glVertex2bvOES(final byte[] coords, final int coords_offset) {
		super.glVertex2bvOES(coords, coords_offset);
	}

	@Override
	public void glVertex3bOES(final byte x, final byte y, final byte z) {
		super.glVertex3bOES(x, y, z);
	}

	@Override
	public void glVertex3bvOES(final ByteBuffer coords) {
		super.glVertex3bvOES(coords);
	}

	@Override
	public void glVertex3bvOES(final byte[] coords, final int coords_offset) {
		super.glVertex3bvOES(coords, coords_offset);
	}

	@Override
	public void glVertex4bOES(final byte x, final byte y, final byte z, final byte w) {
		super.glVertex4bOES(x, y, z, w);
	}

	@Override
	public void glVertex4bvOES(final ByteBuffer coords) {
		super.glVertex4bvOES(coords);
	}

	@Override
	public void glVertex4bvOES(final byte[] coords, final int coords_offset) {
		super.glVertex4bvOES(coords, coords_offset);
	}

	@Override
	public int glQueryMatrixxOES(final IntBuffer mantissa, final IntBuffer exponent) {
		return super.glQueryMatrixxOES(mantissa, exponent);
	}

	@Override
	public int glQueryMatrixxOES(final int[] mantissa, final int mantissa_offset, final int[] exponent,
			final int exponent_offset) {
		return super.glQueryMatrixxOES(mantissa, mantissa_offset, exponent, exponent_offset);
	}

	@Override
	public void glClipPlanef(final int plane, final FloatBuffer equation) {
		super.glClipPlanef(plane, equation);
	}

	@Override
	public void glClipPlanef(final int plane, final float[] equation, final int equation_offset) {
		super.glClipPlanef(plane, equation, equation_offset);
	}

	@Override
	public void glFrustumf(final float l, final float r, final float b, final float t, final float n, final float f) {
		super.glFrustumf(l, r, b, t, n, f);
	}

	@Override
	public void glGetClipPlanef(final int plane, final FloatBuffer equation) {
		super.glGetClipPlanef(plane, equation);
	}

	@Override
	public void glGetClipPlanef(final int plane, final float[] equation, final int equation_offset) {
		super.glGetClipPlanef(plane, equation, equation_offset);
	}

	@Override
	public void glOrthof(final float l, final float r, final float b, final float t, final float n, final float f) {
		super.glOrthof(l, r, b, t, n, f);
	}

	@Override
	public void glDebugMessageEnableAMD(final int category, final int severity, final int count, final IntBuffer ids,
			final boolean enabled) {
		super.glDebugMessageEnableAMD(category, severity, count, ids, enabled);
	}

	@Override
	public void glDebugMessageEnableAMD(final int category, final int severity, final int count, final int[] ids,
			final int ids_offset, final boolean enabled) {
		super.glDebugMessageEnableAMD(category, severity, count, ids, ids_offset, enabled);
	}

	@Override
	public void glDebugMessageInsertAMD(final int category, final int severity, final int id, final int length,
			final String buf) {
		super.glDebugMessageInsertAMD(category, severity, id, length, buf);
	}

	@Override
	public int glGetDebugMessageLogAMD(final int count, final int bufsize, final IntBuffer categories,
			final IntBuffer severities, final IntBuffer ids, final IntBuffer lengths, final ByteBuffer message) {
		return super.glGetDebugMessageLogAMD(count, bufsize, categories, severities, ids, lengths, message);
	}

	@Override
	public int glGetDebugMessageLogAMD(final int count, final int bufsize, final int[] categories,
			final int categories_offset, final int[] severities, final int severities_offset, final int[] ids,
			final int ids_offset, final int[] lengths, final int lengths_offset, final byte[] message,
			final int message_offset) {
		return super.glGetDebugMessageLogAMD(count, bufsize, categories, categories_offset, severities,
				severities_offset, ids, ids_offset, lengths, lengths_offset, message, message_offset);
	}

	@Override
	public void glBlendFuncIndexedAMD(final int buf, final int src, final int dst) {
		super.glBlendFuncIndexedAMD(buf, src, dst);
	}

	@Override
	public void glBlendFuncSeparateIndexedAMD(final int buf, final int srcRGB, final int dstRGB, final int srcAlpha,
			final int dstAlpha) {
		super.glBlendFuncSeparateIndexedAMD(buf, srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	@Override
	public void glBlendEquationIndexedAMD(final int buf, final int mode) {
		super.glBlendEquationIndexedAMD(buf, mode);
	}

	@Override
	public void glBlendEquationSeparateIndexedAMD(final int buf, final int modeRGB, final int modeAlpha) {
		super.glBlendEquationSeparateIndexedAMD(buf, modeRGB, modeAlpha);
	}

	@Override
	public void glUniform1i64NV(final int location, final long x) {
		super.glUniform1i64NV(location, x);
	}

	@Override
	public void glUniform2i64NV(final int location, final long x, final long y) {
		super.glUniform2i64NV(location, x, y);
	}

	@Override
	public void glUniform3i64NV(final int location, final long x, final long y, final long z) {
		super.glUniform3i64NV(location, x, y, z);
	}

	@Override
	public void glUniform4i64NV(final int location, final long x, final long y, final long z, final long w) {
		super.glUniform4i64NV(location, x, y, z, w);
	}

	@Override
	public void glUniform1i64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform1i64vNV(location, count, value);
	}

	@Override
	public void glUniform1i64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform1i64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform2i64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform2i64vNV(location, count, value);
	}

	@Override
	public void glUniform2i64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform2i64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform3i64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform3i64vNV(location, count, value);
	}

	@Override
	public void glUniform3i64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform3i64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform4i64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform4i64vNV(location, count, value);
	}

	@Override
	public void glUniform4i64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform4i64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform1ui64NV(final int location, final long x) {
		super.glUniform1ui64NV(location, x);
	}

	@Override
	public void glUniform2ui64NV(final int location, final long x, final long y) {
		super.glUniform2ui64NV(location, x, y);
	}

	@Override
	public void glUniform3ui64NV(final int location, final long x, final long y, final long z) {
		super.glUniform3ui64NV(location, x, y, z);
	}

	@Override
	public void glUniform4ui64NV(final int location, final long x, final long y, final long z, final long w) {
		super.glUniform4ui64NV(location, x, y, z, w);
	}

	@Override
	public void glUniform1ui64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform1ui64vNV(location, count, value);
	}

	@Override
	public void glUniform1ui64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform1ui64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform2ui64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform2ui64vNV(location, count, value);
	}

	@Override
	public void glUniform2ui64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform2ui64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform3ui64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform3ui64vNV(location, count, value);
	}

	@Override
	public void glUniform3ui64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform3ui64vNV(location, count, value, value_offset);
	}

	@Override
	public void glUniform4ui64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniform4ui64vNV(location, count, value);
	}

	@Override
	public void glUniform4ui64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniform4ui64vNV(location, count, value, value_offset);
	}

	@Override
	public void glGetUniformi64vNV(final int program, final int location, final LongBuffer params) {
		super.glGetUniformi64vNV(program, location, params);
	}

	@Override
	public void glGetUniformi64vNV(final int program, final int location, final long[] params,
			final int params_offset) {
		super.glGetUniformi64vNV(program, location, params, params_offset);
	}

	@Override
	public void glGetUniformui64vNV(final int program, final int location, final LongBuffer params) {
		super.glGetUniformui64vNV(program, location, params);
	}

	@Override
	public void glGetUniformui64vNV(final int program, final int location, final long[] params,
			final int params_offset) {
		super.glGetUniformui64vNV(program, location, params, params_offset);
	}

	@Override
	public void glProgramUniform1i64NV(final int program, final int location, final long x) {
		super.glProgramUniform1i64NV(program, location, x);
	}

	@Override
	public void glProgramUniform2i64NV(final int program, final int location, final long x, final long y) {
		super.glProgramUniform2i64NV(program, location, x, y);
	}

	@Override
	public void glProgramUniform3i64NV(final int program, final int location, final long x, final long y,
			final long z) {
		super.glProgramUniform3i64NV(program, location, x, y, z);
	}

	@Override
	public void glProgramUniform4i64NV(final int program, final int location, final long x, final long y, final long z,
			final long w) {
		super.glProgramUniform4i64NV(program, location, x, y, z, w);
	}

	@Override
	public void glProgramUniform1i64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform1i64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform1i64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform1i64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2i64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform2i64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform2i64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform2i64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3i64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform3i64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform3i64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform3i64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4i64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform4i64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform4i64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform4i64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform1ui64NV(final int program, final int location, final long x) {
		super.glProgramUniform1ui64NV(program, location, x);
	}

	@Override
	public void glProgramUniform2ui64NV(final int program, final int location, final long x, final long y) {
		super.glProgramUniform2ui64NV(program, location, x, y);
	}

	@Override
	public void glProgramUniform3ui64NV(final int program, final int location, final long x, final long y,
			final long z) {
		super.glProgramUniform3ui64NV(program, location, x, y, z);
	}

	@Override
	public void glProgramUniform4ui64NV(final int program, final int location, final long x, final long y, final long z,
			final long w) {
		super.glProgramUniform4ui64NV(program, location, x, y, z, w);
	}

	@Override
	public void glProgramUniform1ui64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform1ui64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform1ui64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform1ui64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2ui64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform2ui64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform2ui64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform2ui64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3ui64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform3ui64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform3ui64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform3ui64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4ui64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniform4ui64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniform4ui64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniform4ui64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glVertexAttribParameteriAMD(final int index, final int pname, final int param) {
		super.glVertexAttribParameteriAMD(index, pname, param);
	}

	@Override
	public void glMultiDrawArraysIndirectAMD(final int mode, final Buffer indirect, final int primcount,
			final int stride) {
		super.glMultiDrawArraysIndirectAMD(mode, indirect, primcount, stride);
	}

	@Override
	public void glMultiDrawElementsIndirectAMD(final int mode, final int type, final Buffer indirect,
			final int primcount, final int stride) {
		super.glMultiDrawElementsIndirectAMD(mode, type, indirect, primcount, stride);
	}

	@Override
	public void glGenNamesAMD(final int identifier, final int num, final IntBuffer names) {
		super.glGenNamesAMD(identifier, num, names);
	}

	@Override
	public void glGenNamesAMD(final int identifier, final int num, final int[] names, final int names_offset) {
		super.glGenNamesAMD(identifier, num, names, names_offset);
	}

	@Override
	public void glDeleteNamesAMD(final int identifier, final int num, final IntBuffer names) {
		super.glDeleteNamesAMD(identifier, num, names);
	}

	@Override
	public void glDeleteNamesAMD(final int identifier, final int num, final int[] names, final int names_offset) {
		super.glDeleteNamesAMD(identifier, num, names, names_offset);
	}

	@Override
	public boolean glIsNameAMD(final int identifier, final int name) {
		return super.glIsNameAMD(identifier, name);
	}

	@Override
	public void glQueryObjectParameteruiAMD(final int target, final int id, final int pname, final int param) {
		super.glQueryObjectParameteruiAMD(target, id, pname, param);
	}

	@Override
	public void glGetPerfMonitorGroupsAMD(final IntBuffer numGroups, final int groupsSize, final IntBuffer groups) {
		super.glGetPerfMonitorGroupsAMD(numGroups, groupsSize, groups);
	}

	@Override
	public void glGetPerfMonitorGroupsAMD(final int[] numGroups, final int numGroups_offset, final int groupsSize,
			final int[] groups, final int groups_offset) {
		super.glGetPerfMonitorGroupsAMD(numGroups, numGroups_offset, groupsSize, groups, groups_offset);
	}

	@Override
	public void glGetPerfMonitorCountersAMD(final int group, final IntBuffer numCounters,
			final IntBuffer maxActiveCounters, final int counterSize, final IntBuffer counters) {
		super.glGetPerfMonitorCountersAMD(group, numCounters, maxActiveCounters, counterSize, counters);
	}

	@Override
	public void glGetPerfMonitorCountersAMD(final int group, final int[] numCounters, final int numCounters_offset,
			final int[] maxActiveCounters, final int maxActiveCounters_offset, final int counterSize,
			final int[] counters, final int counters_offset) {
		super.glGetPerfMonitorCountersAMD(group, numCounters, numCounters_offset, maxActiveCounters,
				maxActiveCounters_offset, counterSize, counters, counters_offset);
	}

	@Override
	public void glGetPerfMonitorGroupStringAMD(final int group, final int bufSize, final IntBuffer length,
			final ByteBuffer groupString) {
		super.glGetPerfMonitorGroupStringAMD(group, bufSize, length, groupString);
	}

	@Override
	public void glGetPerfMonitorGroupStringAMD(final int group, final int bufSize, final int[] length,
			final int length_offset, final byte[] groupString, final int groupString_offset) {
		super.glGetPerfMonitorGroupStringAMD(group, bufSize, length, length_offset, groupString, groupString_offset);
	}

	@Override
	public void glGetPerfMonitorCounterStringAMD(final int group, final int counter, final int bufSize,
			final IntBuffer length, final ByteBuffer counterString) {
		super.glGetPerfMonitorCounterStringAMD(group, counter, bufSize, length, counterString);
	}

	@Override
	public void glGetPerfMonitorCounterStringAMD(final int group, final int counter, final int bufSize,
			final int[] length, final int length_offset, final byte[] counterString, final int counterString_offset) {
		super.glGetPerfMonitorCounterStringAMD(group, counter, bufSize, length, length_offset, counterString,
				counterString_offset);
	}

	@Override
	public void glGetPerfMonitorCounterInfoAMD(final int group, final int counter, final int pname, final Buffer data) {
		super.glGetPerfMonitorCounterInfoAMD(group, counter, pname, data);
	}

	@Override
	public void glGenPerfMonitorsAMD(final int n, final IntBuffer monitors) {
		super.glGenPerfMonitorsAMD(n, monitors);
	}

	@Override
	public void glGenPerfMonitorsAMD(final int n, final int[] monitors, final int monitors_offset) {
		super.glGenPerfMonitorsAMD(n, monitors, monitors_offset);
	}

	@Override
	public void glDeletePerfMonitorsAMD(final int n, final IntBuffer monitors) {
		super.glDeletePerfMonitorsAMD(n, monitors);
	}

	@Override
	public void glDeletePerfMonitorsAMD(final int n, final int[] monitors, final int monitors_offset) {
		super.glDeletePerfMonitorsAMD(n, monitors, monitors_offset);
	}

	@Override
	public void glSelectPerfMonitorCountersAMD(final int monitor, final boolean enable, final int group,
			final int numCounters, final IntBuffer counterList) {
		super.glSelectPerfMonitorCountersAMD(monitor, enable, group, numCounters, counterList);
	}

	@Override
	public void glSelectPerfMonitorCountersAMD(final int monitor, final boolean enable, final int group,
			final int numCounters, final int[] counterList, final int counterList_offset) {
		super.glSelectPerfMonitorCountersAMD(monitor, enable, group, numCounters, counterList, counterList_offset);
	}

	@Override
	public void glBeginPerfMonitorAMD(final int monitor) {
		super.glBeginPerfMonitorAMD(monitor);
	}

	@Override
	public void glEndPerfMonitorAMD(final int monitor) {
		super.glEndPerfMonitorAMD(monitor);
	}

	@Override
	public void glGetPerfMonitorCounterDataAMD(final int monitor, final int pname, final int dataSize,
			final IntBuffer data, final IntBuffer bytesWritten) {
		super.glGetPerfMonitorCounterDataAMD(monitor, pname, dataSize, data, bytesWritten);
	}

	@Override
	public void glGetPerfMonitorCounterDataAMD(final int monitor, final int pname, final int dataSize, final int[] data,
			final int data_offset, final int[] bytesWritten, final int bytesWritten_offset) {
		super.glGetPerfMonitorCounterDataAMD(monitor, pname, dataSize, data, data_offset, bytesWritten,
				bytesWritten_offset);
	}

	@Override
	public void glSetMultisamplefvAMD(final int pname, final int index, final FloatBuffer val) {
		super.glSetMultisamplefvAMD(pname, index, val);
	}

	@Override
	public void glSetMultisamplefvAMD(final int pname, final int index, final float[] val, final int val_offset) {
		super.glSetMultisamplefvAMD(pname, index, val, val_offset);
	}

	@Override
	public void glTexStorageSparseAMD(final int target, final int internalFormat, final int width, final int height,
			final int depth, final int layers, final int flags) {
		super.glTexStorageSparseAMD(target, internalFormat, width, height, depth, layers, flags);
	}

	@Override
	public void glTextureStorageSparseAMD(final int texture, final int target, final int internalFormat,
			final int width, final int height, final int depth, final int layers, final int flags) {
		super.glTextureStorageSparseAMD(texture, target, internalFormat, width, height, depth, layers, flags);
	}

	@Override
	public void glStencilOpValueAMD(final int face, final int value) {
		super.glStencilOpValueAMD(face, value);
	}

	@Override
	public void glTessellationFactorAMD(final float factor) {
		super.glTessellationFactorAMD(factor);
	}

	@Override
	public void glTessellationModeAMD(final int mode) {
		super.glTessellationModeAMD(mode);
	}

	@Override
	public void glBufferParameteri(final int target, final int pname, final int param) {
		super.glBufferParameteri(target, pname, param);
	}

	@Override
	public int glObjectPurgeableAPPLE(final int objectType, final int name, final int option) {
		return super.glObjectPurgeableAPPLE(objectType, name, option);
	}

	@Override
	public int glObjectUnpurgeableAPPLE(final int objectType, final int name, final int option) {
		return super.glObjectUnpurgeableAPPLE(objectType, name, option);
	}

	@Override
	public void glGetObjectParameterivAPPLE(final int objectType, final int name, final int pname,
			final IntBuffer params) {
		super.glGetObjectParameterivAPPLE(objectType, name, pname, params);
	}

	@Override
	public void glGetObjectParameterivAPPLE(final int objectType, final int name, final int pname, final int[] params,
			final int params_offset) {
		super.glGetObjectParameterivAPPLE(objectType, name, pname, params, params_offset);
	}

	@Override
	public void glTextureRangeAPPLE(final int target, final int length, final Buffer pointer) {
		super.glTextureRangeAPPLE(target, length, pointer);
	}

	@Override
	public void glVertexArrayRangeAPPLE(final int length, final Buffer pointer) {
		super.glVertexArrayRangeAPPLE(length, pointer);
	}

	@Override
	public void glFlushVertexArrayRangeAPPLE(final int length, final Buffer pointer) {
		super.glFlushVertexArrayRangeAPPLE(length, pointer);
	}

	@Override
	public void glVertexArrayParameteriAPPLE(final int pname, final int param) {
		super.glVertexArrayParameteriAPPLE(pname, param);
	}

	@Override
	public void glEnableVertexAttribAPPLE(final int index, final int pname) {
		super.glEnableVertexAttribAPPLE(index, pname);
	}

	@Override
	public void glDisableVertexAttribAPPLE(final int index, final int pname) {
		super.glDisableVertexAttribAPPLE(index, pname);
	}

	@Override
	public boolean glIsVertexAttribEnabledAPPLE(final int index, final int pname) {
		return super.glIsVertexAttribEnabledAPPLE(index, pname);
	}

	@Override
	public void glMapVertexAttrib1dAPPLE(final int index, final int size, final double u1, final double u2,
			final int stride, final int order, final DoubleBuffer points) {
		super.glMapVertexAttrib1dAPPLE(index, size, u1, u2, stride, order, points);
	}

	@Override
	public void glMapVertexAttrib1dAPPLE(final int index, final int size, final double u1, final double u2,
			final int stride, final int order, final double[] points, final int points_offset) {
		super.glMapVertexAttrib1dAPPLE(index, size, u1, u2, stride, order, points, points_offset);
	}

	@Override
	public void glMapVertexAttrib1fAPPLE(final int index, final int size, final float u1, final float u2,
			final int stride, final int order, final FloatBuffer points) {
		super.glMapVertexAttrib1fAPPLE(index, size, u1, u2, stride, order, points);
	}

	@Override
	public void glMapVertexAttrib1fAPPLE(final int index, final int size, final float u1, final float u2,
			final int stride, final int order, final float[] points, final int points_offset) {
		super.glMapVertexAttrib1fAPPLE(index, size, u1, u2, stride, order, points, points_offset);
	}

	@Override
	public void glMapVertexAttrib2dAPPLE(final int index, final int size, final double u1, final double u2,
			final int ustride, final int uorder, final double v1, final double v2, final int vstride, final int vorder,
			final DoubleBuffer points) {
		super.glMapVertexAttrib2dAPPLE(index, size, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
	}

	@Override
	public void glMapVertexAttrib2dAPPLE(final int index, final int size, final double u1, final double u2,
			final int ustride, final int uorder, final double v1, final double v2, final int vstride, final int vorder,
			final double[] points, final int points_offset) {
		super.glMapVertexAttrib2dAPPLE(index, size, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points,
				points_offset);
	}

	@Override
	public void glMapVertexAttrib2fAPPLE(final int index, final int size, final float u1, final float u2,
			final int ustride, final int uorder, final float v1, final float v2, final int vstride, final int vorder,
			final FloatBuffer points) {
		super.glMapVertexAttrib2fAPPLE(index, size, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points);
	}

	@Override
	public void glMapVertexAttrib2fAPPLE(final int index, final int size, final float u1, final float u2,
			final int ustride, final int uorder, final float v1, final float v2, final int vstride, final int vorder,
			final float[] points, final int points_offset) {
		super.glMapVertexAttrib2fAPPLE(index, size, u1, u2, ustride, uorder, v1, v2, vstride, vorder, points,
				points_offset);
	}

	@Override
	public void glDrawBuffersATI(final int n, final IntBuffer bufs) {
		super.glDrawBuffersATI(n, bufs);
	}

	@Override
	public void glDrawBuffersATI(final int n, final int[] bufs, final int bufs_offset) {
		super.glDrawBuffersATI(n, bufs, bufs_offset);
	}

	@Override
	public void glPNTrianglesiATI(final int pname, final int param) {
		super.glPNTrianglesiATI(pname, param);
	}

	@Override
	public void glPNTrianglesfATI(final int pname, final float param) {
		super.glPNTrianglesfATI(pname, param);
	}

	@Override
	public void glUniformBufferEXT(final int program, final int location, final int buffer) {
		super.glUniformBufferEXT(program, location, buffer);
	}

	@Override
	public int glGetUniformBufferSizeEXT(final int program, final int location) {
		return super.glGetUniformBufferSizeEXT(program, location);
	}

	@Override
	public long glGetUniformOffsetEXT(final int program, final int location) {
		return super.glGetUniformOffsetEXT(program, location);
	}

	@Override
	public void glLockArraysEXT(final int first, final int count) {
		super.glLockArraysEXT(first, count);
	}

	@Override
	public void glUnlockArraysEXT() {
		super.glUnlockArraysEXT();
	}

	@Override
	public void glCullParameterdvEXT(final int pname, final DoubleBuffer params) {
		super.glCullParameterdvEXT(pname, params);
	}

	@Override
	public void glCullParameterdvEXT(final int pname, final double[] params, final int params_offset) {
		super.glCullParameterdvEXT(pname, params, params_offset);
	}

	@Override
	public void glCullParameterfvEXT(final int pname, final FloatBuffer params) {
		super.glCullParameterfvEXT(pname, params);
	}

	@Override
	public void glCullParameterfvEXT(final int pname, final float[] params, final int params_offset) {
		super.glCullParameterfvEXT(pname, params, params_offset);
	}

	@Override
	public void glDepthBoundsEXT(final double zmin, final double zmax) {
		super.glDepthBoundsEXT(zmin, zmax);
	}

	@Override
	public void glMatrixLoadfEXT(final int mode, final FloatBuffer m) {
		super.glMatrixLoadfEXT(mode, m);
	}

	@Override
	public void glMatrixLoadfEXT(final int mode, final float[] m, final int m_offset) {
		super.glMatrixLoadfEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixLoaddEXT(final int mode, final DoubleBuffer m) {
		super.glMatrixLoaddEXT(mode, m);
	}

	@Override
	public void glMatrixLoaddEXT(final int mode, final double[] m, final int m_offset) {
		super.glMatrixLoaddEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixMultfEXT(final int mode, final FloatBuffer m) {
		super.glMatrixMultfEXT(mode, m);
	}

	@Override
	public void glMatrixMultfEXT(final int mode, final float[] m, final int m_offset) {
		super.glMatrixMultfEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixMultdEXT(final int mode, final DoubleBuffer m) {
		super.glMatrixMultdEXT(mode, m);
	}

	@Override
	public void glMatrixMultdEXT(final int mode, final double[] m, final int m_offset) {
		super.glMatrixMultdEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixLoadIdentityEXT(final int mode) {
		super.glMatrixLoadIdentityEXT(mode);
	}

	@Override
	public void glMatrixRotatefEXT(final int mode, final float angle, final float x, final float y, final float z) {
		super.glMatrixRotatefEXT(mode, angle, x, y, z);
	}

	@Override
	public void glMatrixRotatedEXT(final int mode, final double angle, final double x, final double y, final double z) {
		super.glMatrixRotatedEXT(mode, angle, x, y, z);
	}

	@Override
	public void glMatrixScalefEXT(final int mode, final float x, final float y, final float z) {
		super.glMatrixScalefEXT(mode, x, y, z);
	}

	@Override
	public void glMatrixScaledEXT(final int mode, final double x, final double y, final double z) {
		super.glMatrixScaledEXT(mode, x, y, z);
	}

	@Override
	public void glMatrixTranslatefEXT(final int mode, final float x, final float y, final float z) {
		super.glMatrixTranslatefEXT(mode, x, y, z);
	}

	@Override
	public void glMatrixTranslatedEXT(final int mode, final double x, final double y, final double z) {
		super.glMatrixTranslatedEXT(mode, x, y, z);
	}

	@Override
	public void glMatrixFrustumEXT(final int mode, final double left, final double right, final double bottom,
			final double top, final double zNear, final double zFar) {
		super.glMatrixFrustumEXT(mode, left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glMatrixOrthoEXT(final int mode, final double left, final double right, final double bottom,
			final double top, final double zNear, final double zFar) {
		super.glMatrixOrthoEXT(mode, left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glMatrixPopEXT(final int mode) {
		super.glMatrixPopEXT(mode);
	}

	@Override
	public void glMatrixPushEXT(final int mode) {
		super.glMatrixPushEXT(mode);
	}

	@Override
	public void glClientAttribDefaultEXT(final int mask) {
		super.glClientAttribDefaultEXT(mask);
	}

	@Override
	public void glPushClientAttribDefaultEXT(final int mask) {
		super.glPushClientAttribDefaultEXT(mask);
	}

	@Override
	public void glTextureParameterfEXT(final int texture, final int target, final int pname, final float param) {
		super.glTextureParameterfEXT(texture, target, pname, param);
	}

	@Override
	public void glTextureParameterfvEXT(final int texture, final int target, final int pname,
			final FloatBuffer params) {
		super.glTextureParameterfvEXT(texture, target, pname, params);
	}

	@Override
	public void glTextureParameterfvEXT(final int texture, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glTextureParameterfvEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glTextureParameteriEXT(final int texture, final int target, final int pname, final int param) {
		super.glTextureParameteriEXT(texture, target, pname, param);
	}

	@Override
	public void glTextureParameterivEXT(final int texture, final int target, final int pname, final IntBuffer params) {
		super.glTextureParameterivEXT(texture, target, pname, params);
	}

	@Override
	public void glTextureParameterivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glTextureParameterivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glTextureImage1DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int border, final int format, final int type, final Buffer pixels) {
		super.glTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, pixels);
	}

	@Override
	public void glTextureImage1DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int border, final int format, final int type, final long pixels_buffer_offset) {
		super.glTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glTextureImage2DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int height, final int border, final int format, final int type,
			final Buffer pixels) {
		super.glTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, pixels);
	}

	@Override
	public void glTextureImage2DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int height, final int border, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glTextureSubImage1DEXT(final int texture, final int target, final int level, final int xoffset,
			final int width, final int format, final int type, final Buffer pixels) {
		super.glTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, pixels);
	}

	@Override
	public void glTextureSubImage1DEXT(final int texture, final int target, final int level, final int xoffset,
			final int width, final int format, final int type, final long pixels_buffer_offset) {
		super.glTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, pixels_buffer_offset);
	}

	@Override
	public void glTextureSubImage2DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int width, final int height, final int format, final int type,
			final Buffer pixels) {
		super.glTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glTextureSubImage2DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int width, final int height, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glCopyTextureImage1DEXT(final int texture, final int target, final int level, final int internalformat,
			final int x, final int y, final int width, final int border) {
		super.glCopyTextureImage1DEXT(texture, target, level, internalformat, x, y, width, border);
	}

	@Override
	public void glCopyTextureImage2DEXT(final int texture, final int target, final int level, final int internalformat,
			final int x, final int y, final int width, final int height, final int border) {
		super.glCopyTextureImage2DEXT(texture, target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyTextureSubImage1DEXT(final int texture, final int target, final int level, final int xoffset,
			final int x, final int y, final int width) {
		super.glCopyTextureSubImage1DEXT(texture, target, level, xoffset, x, y, width);
	}

	@Override
	public void glCopyTextureSubImage2DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int x, final int y, final int width, final int height) {
		super.glCopyTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glGetTextureImageEXT(final int texture, final int target, final int level, final int format,
			final int type, final Buffer pixels) {
		super.glGetTextureImageEXT(texture, target, level, format, type, pixels);
	}

	@Override
	public void glGetTextureParameterfvEXT(final int texture, final int target, final int pname,
			final FloatBuffer params) {
		super.glGetTextureParameterfvEXT(texture, target, pname, params);
	}

	@Override
	public void glGetTextureParameterfvEXT(final int texture, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetTextureParameterfvEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterivEXT(final int texture, final int target, final int pname,
			final IntBuffer params) {
		super.glGetTextureParameterivEXT(texture, target, pname, params);
	}

	@Override
	public void glGetTextureParameterivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameterivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glGetTextureLevelParameterfvEXT(final int texture, final int target, final int level, final int pname,
			final FloatBuffer params) {
		super.glGetTextureLevelParameterfvEXT(texture, target, level, pname, params);
	}

	@Override
	public void glGetTextureLevelParameterfvEXT(final int texture, final int target, final int level, final int pname,
			final float[] params, final int params_offset) {
		super.glGetTextureLevelParameterfvEXT(texture, target, level, pname, params, params_offset);
	}

	@Override
	public void glGetTextureLevelParameterivEXT(final int texture, final int target, final int level, final int pname,
			final IntBuffer params) {
		super.glGetTextureLevelParameterivEXT(texture, target, level, pname, params);
	}

	@Override
	public void glGetTextureLevelParameterivEXT(final int texture, final int target, final int level, final int pname,
			final int[] params, final int params_offset) {
		super.glGetTextureLevelParameterivEXT(texture, target, level, pname, params, params_offset);
	}

	@Override
	public void glTextureImage3DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int height, final int depth, final int border, final int format, final int type,
			final Buffer pixels) {
		super.glTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type,
				pixels);
	}

	@Override
	public void glTextureImage3DEXT(final int texture, final int target, final int level, final int internalformat,
			final int width, final int height, final int depth, final int border, final int format, final int type,
			final long pixels_buffer_offset) {
		super.glTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type,
				pixels_buffer_offset);
	}

	@Override
	public void glTextureSubImage3DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int zoffset, final int width, final int height, final int depth, final int format,
			final int type, final Buffer pixels) {
		super.glTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format,
				type, pixels);
	}

	@Override
	public void glTextureSubImage3DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int zoffset, final int width, final int height, final int depth, final int format,
			final int type, final long pixels_buffer_offset) {
		super.glTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format,
				type, pixels_buffer_offset);
	}

	@Override
	public void glCopyTextureSubImage3DEXT(final int texture, final int target, final int level, final int xoffset,
			final int yoffset, final int zoffset, final int x, final int y, final int width, final int height) {
		super.glCopyTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, x, y, width, height);
	}

	@Override
	public void glBindMultiTextureEXT(final int texunit, final int target, final int texture) {
		super.glBindMultiTextureEXT(texunit, target, texture);
	}

	@Override
	public void glMultiTexCoordPointerEXT(final int texunit, final int size, final int type, final int stride,
			final Buffer pointer) {
		super.glMultiTexCoordPointerEXT(texunit, size, type, stride, pointer);
	}

	@Override
	public void glMultiTexEnvfEXT(final int texunit, final int target, final int pname, final float param) {
		super.glMultiTexEnvfEXT(texunit, target, pname, param);
	}

	@Override
	public void glMultiTexEnvfvEXT(final int texunit, final int target, final int pname, final FloatBuffer params) {
		super.glMultiTexEnvfvEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexEnvfvEXT(final int texunit, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glMultiTexEnvfvEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexEnviEXT(final int texunit, final int target, final int pname, final int param) {
		super.glMultiTexEnviEXT(texunit, target, pname, param);
	}

	@Override
	public void glMultiTexEnvivEXT(final int texunit, final int target, final int pname, final IntBuffer params) {
		super.glMultiTexEnvivEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexEnvivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glMultiTexEnvivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexGendEXT(final int texunit, final int coord, final int pname, final double param) {
		super.glMultiTexGendEXT(texunit, coord, pname, param);
	}

	@Override
	public void glMultiTexGendvEXT(final int texunit, final int coord, final int pname, final DoubleBuffer params) {
		super.glMultiTexGendvEXT(texunit, coord, pname, params);
	}

	@Override
	public void glMultiTexGendvEXT(final int texunit, final int coord, final int pname, final double[] params,
			final int params_offset) {
		super.glMultiTexGendvEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glMultiTexGenfEXT(final int texunit, final int coord, final int pname, final float param) {
		super.glMultiTexGenfEXT(texunit, coord, pname, param);
	}

	@Override
	public void glMultiTexGenfvEXT(final int texunit, final int coord, final int pname, final FloatBuffer params) {
		super.glMultiTexGenfvEXT(texunit, coord, pname, params);
	}

	@Override
	public void glMultiTexGenfvEXT(final int texunit, final int coord, final int pname, final float[] params,
			final int params_offset) {
		super.glMultiTexGenfvEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glMultiTexGeniEXT(final int texunit, final int coord, final int pname, final int param) {
		super.glMultiTexGeniEXT(texunit, coord, pname, param);
	}

	@Override
	public void glMultiTexGenivEXT(final int texunit, final int coord, final int pname, final IntBuffer params) {
		super.glMultiTexGenivEXT(texunit, coord, pname, params);
	}

	@Override
	public void glMultiTexGenivEXT(final int texunit, final int coord, final int pname, final int[] params,
			final int params_offset) {
		super.glMultiTexGenivEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexEnvfvEXT(final int texunit, final int target, final int pname, final FloatBuffer params) {
		super.glGetMultiTexEnvfvEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexEnvfvEXT(final int texunit, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMultiTexEnvfvEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexEnvivEXT(final int texunit, final int target, final int pname, final IntBuffer params) {
		super.glGetMultiTexEnvivEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexEnvivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMultiTexEnvivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexGendvEXT(final int texunit, final int coord, final int pname, final DoubleBuffer params) {
		super.glGetMultiTexGendvEXT(texunit, coord, pname, params);
	}

	@Override
	public void glGetMultiTexGendvEXT(final int texunit, final int coord, final int pname, final double[] params,
			final int params_offset) {
		super.glGetMultiTexGendvEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexGenfvEXT(final int texunit, final int coord, final int pname, final FloatBuffer params) {
		super.glGetMultiTexGenfvEXT(texunit, coord, pname, params);
	}

	@Override
	public void glGetMultiTexGenfvEXT(final int texunit, final int coord, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMultiTexGenfvEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexGenivEXT(final int texunit, final int coord, final int pname, final IntBuffer params) {
		super.glGetMultiTexGenivEXT(texunit, coord, pname, params);
	}

	@Override
	public void glGetMultiTexGenivEXT(final int texunit, final int coord, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMultiTexGenivEXT(texunit, coord, pname, params, params_offset);
	}

	@Override
	public void glMultiTexParameteriEXT(final int texunit, final int target, final int pname, final int param) {
		super.glMultiTexParameteriEXT(texunit, target, pname, param);
	}

	@Override
	public void glMultiTexParameterivEXT(final int texunit, final int target, final int pname, final IntBuffer params) {
		super.glMultiTexParameterivEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexParameterivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glMultiTexParameterivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexParameterfEXT(final int texunit, final int target, final int pname, final float param) {
		super.glMultiTexParameterfEXT(texunit, target, pname, param);
	}

	@Override
	public void glMultiTexParameterfvEXT(final int texunit, final int target, final int pname,
			final FloatBuffer params) {
		super.glMultiTexParameterfvEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexParameterfvEXT(final int texunit, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glMultiTexParameterfvEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexImage1DEXT(final int texunit, final int target, final int level, final int internalformat,
			final int width, final int border, final int format, final int type, final Buffer pixels) {
		super.glMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, pixels);
	}

	@Override
	public void glMultiTexImage2DEXT(final int texunit, final int target, final int level, final int internalformat,
			final int width, final int height, final int border, final int format, final int type,
			final Buffer pixels) {
		super.glMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, pixels);
	}

	@Override
	public void glMultiTexSubImage1DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int width, final int format, final int type, final Buffer pixels) {
		super.glMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, pixels);
	}

	@Override
	public void glMultiTexSubImage2DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int yoffset, final int width, final int height, final int format, final int type,
			final Buffer pixels) {
		super.glMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void glCopyMultiTexImage1DEXT(final int texunit, final int target, final int level, final int internalformat,
			final int x, final int y, final int width, final int border) {
		super.glCopyMultiTexImage1DEXT(texunit, target, level, internalformat, x, y, width, border);
	}

	@Override
	public void glCopyMultiTexImage2DEXT(final int texunit, final int target, final int level, final int internalformat,
			final int x, final int y, final int width, final int height, final int border) {
		super.glCopyMultiTexImage2DEXT(texunit, target, level, internalformat, x, y, width, height, border);
	}

	@Override
	public void glCopyMultiTexSubImage1DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int x, final int y, final int width) {
		super.glCopyMultiTexSubImage1DEXT(texunit, target, level, xoffset, x, y, width);
	}

	@Override
	public void glCopyMultiTexSubImage2DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int yoffset, final int x, final int y, final int width, final int height) {
		super.glCopyMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, x, y, width, height);
	}

	@Override
	public void glGetMultiTexImageEXT(final int texunit, final int target, final int level, final int format,
			final int type, final Buffer pixels) {
		super.glGetMultiTexImageEXT(texunit, target, level, format, type, pixels);
	}

	@Override
	public void glGetMultiTexParameterfvEXT(final int texunit, final int target, final int pname,
			final FloatBuffer params) {
		super.glGetMultiTexParameterfvEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexParameterfvEXT(final int texunit, final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMultiTexParameterfvEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexParameterivEXT(final int texunit, final int target, final int pname,
			final IntBuffer params) {
		super.glGetMultiTexParameterivEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexParameterivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMultiTexParameterivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexLevelParameterfvEXT(final int texunit, final int target, final int level, final int pname,
			final FloatBuffer params) {
		super.glGetMultiTexLevelParameterfvEXT(texunit, target, level, pname, params);
	}

	@Override
	public void glGetMultiTexLevelParameterfvEXT(final int texunit, final int target, final int level, final int pname,
			final float[] params, final int params_offset) {
		super.glGetMultiTexLevelParameterfvEXT(texunit, target, level, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexLevelParameterivEXT(final int texunit, final int target, final int level, final int pname,
			final IntBuffer params) {
		super.glGetMultiTexLevelParameterivEXT(texunit, target, level, pname, params);
	}

	@Override
	public void glGetMultiTexLevelParameterivEXT(final int texunit, final int target, final int level, final int pname,
			final int[] params, final int params_offset) {
		super.glGetMultiTexLevelParameterivEXT(texunit, target, level, pname, params, params_offset);
	}

	@Override
	public void glMultiTexImage3DEXT(final int texunit, final int target, final int level, final int internalformat,
			final int width, final int height, final int depth, final int border, final int format, final int type,
			final Buffer pixels) {
		super.glMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type,
				pixels);
	}

	@Override
	public void glMultiTexSubImage3DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int yoffset, final int zoffset, final int width, final int height, final int depth, final int format,
			final int type, final Buffer pixels) {
		super.glMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format,
				type, pixels);
	}

	@Override
	public void glCopyMultiTexSubImage3DEXT(final int texunit, final int target, final int level, final int xoffset,
			final int yoffset, final int zoffset, final int x, final int y, final int width, final int height) {
		super.glCopyMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, x, y, width, height);
	}

	@Override
	public void glEnableClientStateIndexedEXT(final int array, final int index) {
		super.glEnableClientStateIndexedEXT(array, index);
	}

	@Override
	public void glDisableClientStateIndexedEXT(final int array, final int index) {
		super.glDisableClientStateIndexedEXT(array, index);
	}

	@Override
	public void glGetFloatIndexedvEXT(final int target, final int index, final FloatBuffer data) {
		super.glGetFloatIndexedvEXT(target, index, data);
	}

	@Override
	public void glGetFloatIndexedvEXT(final int target, final int index, final float[] data, final int data_offset) {
		super.glGetFloatIndexedvEXT(target, index, data, data_offset);
	}

	@Override
	public void glGetDoubleIndexedvEXT(final int target, final int index, final DoubleBuffer data) {
		super.glGetDoubleIndexedvEXT(target, index, data);
	}

	@Override
	public void glGetDoubleIndexedvEXT(final int target, final int index, final double[] data, final int data_offset) {
		super.glGetDoubleIndexedvEXT(target, index, data, data_offset);
	}

	@Override
	public void glEnableIndexed(final int target, final int index) {
		super.glEnableIndexed(target, index);
	}

	@Override
	public void glDisableIndexed(final int target, final int index) {
		super.glDisableIndexed(target, index);
	}

	@Override
	public boolean glIsEnabledIndexed(final int target, final int index) {
		return super.glIsEnabledIndexed(target, index);
	}

	@Override
	public void glGetIntegerIndexedv(final int target, final int index, final IntBuffer data) {
		super.glGetIntegerIndexedv(target, index, data);
	}

	@Override
	public void glGetIntegerIndexedv(final int target, final int index, final int[] data, final int data_offset) {
		super.glGetIntegerIndexedv(target, index, data, data_offset);
	}

	@Override
	public void glGetBooleanIndexedv(final int target, final int index, final ByteBuffer data) {
		super.glGetBooleanIndexedv(target, index, data);
	}

	@Override
	public void glGetBooleanIndexedv(final int target, final int index, final byte[] data, final int data_offset) {
		super.glGetBooleanIndexedv(target, index, data, data_offset);
	}

	@Override
	public void glCompressedTextureImage3DEXT(final int texture, final int target, final int level,
			final int internalformat, final int width, final int height, final int depth, final int border,
			final int imageSize, final Buffer bits) {
		super.glCompressedTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border,
				imageSize, bits);
	}

	@Override
	public void glCompressedTextureImage2DEXT(final int texture, final int target, final int level,
			final int internalformat, final int width, final int height, final int border, final int imageSize,
			final Buffer bits) {
		super.glCompressedTextureImage2DEXT(texture, target, level, internalformat, width, height, border, imageSize,
				bits);
	}

	@Override
	public void glCompressedTextureImage1DEXT(final int texture, final int target, final int level,
			final int internalformat, final int width, final int border, final int imageSize, final Buffer bits) {
		super.glCompressedTextureImage1DEXT(texture, target, level, internalformat, width, border, imageSize, bits);
	}

	@Override
	public void glCompressedTextureSubImage3DEXT(final int texture, final int target, final int level,
			final int xoffset, final int yoffset, final int zoffset, final int width, final int height, final int depth,
			final int format, final int imageSize, final Buffer bits) {
		super.glCompressedTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth,
				format, imageSize, bits);
	}

	@Override
	public void glCompressedTextureSubImage2DEXT(final int texture, final int target, final int level,
			final int xoffset, final int yoffset, final int width, final int height, final int format,
			final int imageSize, final Buffer bits) {
		super.glCompressedTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format,
				imageSize, bits);
	}

	@Override
	public void glCompressedTextureSubImage1DEXT(final int texture, final int target, final int level,
			final int xoffset, final int width, final int format, final int imageSize, final Buffer bits) {
		super.glCompressedTextureSubImage1DEXT(texture, target, level, xoffset, width, format, imageSize, bits);
	}

	@Override
	public void glGetCompressedTextureImageEXT(final int texture, final int target, final int lod, final Buffer img) {
		super.glGetCompressedTextureImageEXT(texture, target, lod, img);
	}

	@Override
	public void glCompressedMultiTexImage3DEXT(final int texunit, final int target, final int level,
			final int internalformat, final int width, final int height, final int depth, final int border,
			final int imageSize, final Buffer bits) {
		super.glCompressedMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border,
				imageSize, bits);
	}

	@Override
	public void glCompressedMultiTexImage2DEXT(final int texunit, final int target, final int level,
			final int internalformat, final int width, final int height, final int border, final int imageSize,
			final Buffer bits) {
		super.glCompressedMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, imageSize,
				bits);
	}

	@Override
	public void glCompressedMultiTexImage1DEXT(final int texunit, final int target, final int level,
			final int internalformat, final int width, final int border, final int imageSize, final Buffer bits) {
		super.glCompressedMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, imageSize, bits);
	}

	@Override
	public void glCompressedMultiTexSubImage3DEXT(final int texunit, final int target, final int level,
			final int xoffset, final int yoffset, final int zoffset, final int width, final int height, final int depth,
			final int format, final int imageSize, final Buffer bits) {
		super.glCompressedMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth,
				format, imageSize, bits);
	}

	@Override
	public void glCompressedMultiTexSubImage2DEXT(final int texunit, final int target, final int level,
			final int xoffset, final int yoffset, final int width, final int height, final int format,
			final int imageSize, final Buffer bits) {
		super.glCompressedMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format,
				imageSize, bits);
	}

	@Override
	public void glCompressedMultiTexSubImage1DEXT(final int texunit, final int target, final int level,
			final int xoffset, final int width, final int format, final int imageSize, final Buffer bits) {
		super.glCompressedMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, imageSize, bits);
	}

	@Override
	public void glGetCompressedMultiTexImageEXT(final int texunit, final int target, final int lod, final Buffer img) {
		super.glGetCompressedMultiTexImageEXT(texunit, target, lod, img);
	}

	@Override
	public void glMatrixLoadTransposefEXT(final int mode, final FloatBuffer m) {
		super.glMatrixLoadTransposefEXT(mode, m);
	}

	@Override
	public void glMatrixLoadTransposefEXT(final int mode, final float[] m, final int m_offset) {
		super.glMatrixLoadTransposefEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixLoadTransposedEXT(final int mode, final DoubleBuffer m) {
		super.glMatrixLoadTransposedEXT(mode, m);
	}

	@Override
	public void glMatrixLoadTransposedEXT(final int mode, final double[] m, final int m_offset) {
		super.glMatrixLoadTransposedEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixMultTransposefEXT(final int mode, final FloatBuffer m) {
		super.glMatrixMultTransposefEXT(mode, m);
	}

	@Override
	public void glMatrixMultTransposefEXT(final int mode, final float[] m, final int m_offset) {
		super.glMatrixMultTransposefEXT(mode, m, m_offset);
	}

	@Override
	public void glMatrixMultTransposedEXT(final int mode, final DoubleBuffer m) {
		super.glMatrixMultTransposedEXT(mode, m);
	}

	@Override
	public void glMatrixMultTransposedEXT(final int mode, final double[] m, final int m_offset) {
		super.glMatrixMultTransposedEXT(mode, m, m_offset);
	}

	@Override
	public void glNamedBufferSubDataEXT(final int buffer, final long offset, final long size, final Buffer data) {
		super.glNamedBufferSubDataEXT(buffer, offset, size, data);
	}

	@Override
	public boolean glUnmapNamedBufferEXT(final int buffer) {
		return super.glUnmapNamedBufferEXT(buffer);
	}

	@Override
	public void glGetNamedBufferParameterivEXT(final int buffer, final int pname, final IntBuffer params) {
		super.glGetNamedBufferParameterivEXT(buffer, pname, params);
	}

	@Override
	public void glGetNamedBufferParameterivEXT(final int buffer, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedBufferParameterivEXT(buffer, pname, params, params_offset);
	}

	@Override
	public void glGetNamedBufferSubDataEXT(final int buffer, final long offset, final long size, final Buffer data) {
		super.glGetNamedBufferSubDataEXT(buffer, offset, size, data);
	}

	@Override
	public void glTextureBufferEXT(final int texture, final int target, final int internalformat, final int buffer) {
		super.glTextureBufferEXT(texture, target, internalformat, buffer);
	}

	@Override
	public void glMultiTexBufferEXT(final int texunit, final int target, final int internalformat, final int buffer) {
		super.glMultiTexBufferEXT(texunit, target, internalformat, buffer);
	}

	@Override
	public void glTextureParameterIivEXT(final int texture, final int target, final int pname, final IntBuffer params) {
		super.glTextureParameterIivEXT(texture, target, pname, params);
	}

	@Override
	public void glTextureParameterIivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glTextureParameterIivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glTextureParameterIuivEXT(final int texture, final int target, final int pname,
			final IntBuffer params) {
		super.glTextureParameterIuivEXT(texture, target, pname, params);
	}

	@Override
	public void glTextureParameterIuivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glTextureParameterIuivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterIivEXT(final int texture, final int target, final int pname,
			final IntBuffer params) {
		super.glGetTextureParameterIivEXT(texture, target, pname, params);
	}

	@Override
	public void glGetTextureParameterIivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameterIivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glGetTextureParameterIuivEXT(final int texture, final int target, final int pname,
			final IntBuffer params) {
		super.glGetTextureParameterIuivEXT(texture, target, pname, params);
	}

	@Override
	public void glGetTextureParameterIuivEXT(final int texture, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetTextureParameterIuivEXT(texture, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexParameterIivEXT(final int texunit, final int target, final int pname,
			final IntBuffer params) {
		super.glMultiTexParameterIivEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexParameterIivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glMultiTexParameterIivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glMultiTexParameterIuivEXT(final int texunit, final int target, final int pname,
			final IntBuffer params) {
		super.glMultiTexParameterIuivEXT(texunit, target, pname, params);
	}

	@Override
	public void glMultiTexParameterIuivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glMultiTexParameterIuivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexParameterIivEXT(final int texunit, final int target, final int pname,
			final IntBuffer params) {
		super.glGetMultiTexParameterIivEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexParameterIivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMultiTexParameterIivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glGetMultiTexParameterIuivEXT(final int texunit, final int target, final int pname,
			final IntBuffer params) {
		super.glGetMultiTexParameterIuivEXT(texunit, target, pname, params);
	}

	@Override
	public void glGetMultiTexParameterIuivEXT(final int texunit, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMultiTexParameterIuivEXT(texunit, target, pname, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParameters4fvEXT(final int program, final int target, final int index,
			final int count, final FloatBuffer params) {
		super.glNamedProgramLocalParameters4fvEXT(program, target, index, count, params);
	}

	@Override
	public void glNamedProgramLocalParameters4fvEXT(final int program, final int target, final int index,
			final int count, final float[] params, final int params_offset) {
		super.glNamedProgramLocalParameters4fvEXT(program, target, index, count, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParameterI4iEXT(final int program, final int target, final int index, final int x,
			final int y, final int z, final int w) {
		super.glNamedProgramLocalParameterI4iEXT(program, target, index, x, y, z, w);
	}

	@Override
	public void glNamedProgramLocalParameterI4ivEXT(final int program, final int target, final int index,
			final IntBuffer params) {
		super.glNamedProgramLocalParameterI4ivEXT(program, target, index, params);
	}

	@Override
	public void glNamedProgramLocalParameterI4ivEXT(final int program, final int target, final int index,
			final int[] params, final int params_offset) {
		super.glNamedProgramLocalParameterI4ivEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParametersI4ivEXT(final int program, final int target, final int index,
			final int count, final IntBuffer params) {
		super.glNamedProgramLocalParametersI4ivEXT(program, target, index, count, params);
	}

	@Override
	public void glNamedProgramLocalParametersI4ivEXT(final int program, final int target, final int index,
			final int count, final int[] params, final int params_offset) {
		super.glNamedProgramLocalParametersI4ivEXT(program, target, index, count, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParameterI4uiEXT(final int program, final int target, final int index, final int x,
			final int y, final int z, final int w) {
		super.glNamedProgramLocalParameterI4uiEXT(program, target, index, x, y, z, w);
	}

	@Override
	public void glNamedProgramLocalParameterI4uivEXT(final int program, final int target, final int index,
			final IntBuffer params) {
		super.glNamedProgramLocalParameterI4uivEXT(program, target, index, params);
	}

	@Override
	public void glNamedProgramLocalParameterI4uivEXT(final int program, final int target, final int index,
			final int[] params, final int params_offset) {
		super.glNamedProgramLocalParameterI4uivEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParametersI4uivEXT(final int program, final int target, final int index,
			final int count, final IntBuffer params) {
		super.glNamedProgramLocalParametersI4uivEXT(program, target, index, count, params);
	}

	@Override
	public void glNamedProgramLocalParametersI4uivEXT(final int program, final int target, final int index,
			final int count, final int[] params, final int params_offset) {
		super.glNamedProgramLocalParametersI4uivEXT(program, target, index, count, params, params_offset);
	}

	@Override
	public void glGetNamedProgramLocalParameterIivEXT(final int program, final int target, final int index,
			final IntBuffer params) {
		super.glGetNamedProgramLocalParameterIivEXT(program, target, index, params);
	}

	@Override
	public void glGetNamedProgramLocalParameterIivEXT(final int program, final int target, final int index,
			final int[] params, final int params_offset) {
		super.glGetNamedProgramLocalParameterIivEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glGetNamedProgramLocalParameterIuivEXT(final int program, final int target, final int index,
			final IntBuffer params) {
		super.glGetNamedProgramLocalParameterIuivEXT(program, target, index, params);
	}

	@Override
	public void glGetNamedProgramLocalParameterIuivEXT(final int program, final int target, final int index,
			final int[] params, final int params_offset) {
		super.glGetNamedProgramLocalParameterIuivEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glEnableClientStateiEXT(final int array, final int index) {
		super.glEnableClientStateiEXT(array, index);
	}

	@Override
	public void glDisableClientStateiEXT(final int array, final int index) {
		super.glDisableClientStateiEXT(array, index);
	}

	@Override
	public void glGetFloati_vEXT(final int pname, final int index, final FloatBuffer params) {
		super.glGetFloati_vEXT(pname, index, params);
	}

	@Override
	public void glGetFloati_vEXT(final int pname, final int index, final float[] params, final int params_offset) {
		super.glGetFloati_vEXT(pname, index, params, params_offset);
	}

	@Override
	public void glGetDoublei_vEXT(final int pname, final int index, final DoubleBuffer params) {
		super.glGetDoublei_vEXT(pname, index, params);
	}

	@Override
	public void glGetDoublei_vEXT(final int pname, final int index, final double[] params, final int params_offset) {
		super.glGetDoublei_vEXT(pname, index, params, params_offset);
	}

	@Override
	public void glGetPointeri_vEXT(final int pname, final int index, final PointerBuffer params) {
		super.glGetPointeri_vEXT(pname, index, params);
	}

	@Override
	public void glNamedProgramStringEXT(final int program, final int target, final int format, final int len,
			final Buffer string) {
		super.glNamedProgramStringEXT(program, target, format, len, string);
	}

	@Override
	public void glNamedProgramLocalParameter4dEXT(final int program, final int target, final int index, final double x,
			final double y, final double z, final double w) {
		super.glNamedProgramLocalParameter4dEXT(program, target, index, x, y, z, w);
	}

	@Override
	public void glNamedProgramLocalParameter4dvEXT(final int program, final int target, final int index,
			final DoubleBuffer params) {
		super.glNamedProgramLocalParameter4dvEXT(program, target, index, params);
	}

	@Override
	public void glNamedProgramLocalParameter4dvEXT(final int program, final int target, final int index,
			final double[] params, final int params_offset) {
		super.glNamedProgramLocalParameter4dvEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glNamedProgramLocalParameter4fEXT(final int program, final int target, final int index, final float x,
			final float y, final float z, final float w) {
		super.glNamedProgramLocalParameter4fEXT(program, target, index, x, y, z, w);
	}

	@Override
	public void glNamedProgramLocalParameter4fvEXT(final int program, final int target, final int index,
			final FloatBuffer params) {
		super.glNamedProgramLocalParameter4fvEXT(program, target, index, params);
	}

	@Override
	public void glNamedProgramLocalParameter4fvEXT(final int program, final int target, final int index,
			final float[] params, final int params_offset) {
		super.glNamedProgramLocalParameter4fvEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glGetNamedProgramLocalParameterdvEXT(final int program, final int target, final int index,
			final DoubleBuffer params) {
		super.glGetNamedProgramLocalParameterdvEXT(program, target, index, params);
	}

	@Override
	public void glGetNamedProgramLocalParameterdvEXT(final int program, final int target, final int index,
			final double[] params, final int params_offset) {
		super.glGetNamedProgramLocalParameterdvEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glGetNamedProgramLocalParameterfvEXT(final int program, final int target, final int index,
			final FloatBuffer params) {
		super.glGetNamedProgramLocalParameterfvEXT(program, target, index, params);
	}

	@Override
	public void glGetNamedProgramLocalParameterfvEXT(final int program, final int target, final int index,
			final float[] params, final int params_offset) {
		super.glGetNamedProgramLocalParameterfvEXT(program, target, index, params, params_offset);
	}

	@Override
	public void glGetNamedProgramivEXT(final int program, final int target, final int pname, final IntBuffer params) {
		super.glGetNamedProgramivEXT(program, target, pname, params);
	}

	@Override
	public void glGetNamedProgramivEXT(final int program, final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedProgramivEXT(program, target, pname, params, params_offset);
	}

	@Override
	public void glGetNamedProgramStringEXT(final int program, final int target, final int pname, final Buffer string) {
		super.glGetNamedProgramStringEXT(program, target, pname, string);
	}

	@Override
	public void glNamedRenderbufferStorageEXT(final int renderbuffer, final int internalformat, final int width,
			final int height) {
		super.glNamedRenderbufferStorageEXT(renderbuffer, internalformat, width, height);
	}

	@Override
	public void glGetNamedRenderbufferParameterivEXT(final int renderbuffer, final int pname, final IntBuffer params) {
		super.glGetNamedRenderbufferParameterivEXT(renderbuffer, pname, params);
	}

	@Override
	public void glGetNamedRenderbufferParameterivEXT(final int renderbuffer, final int pname, final int[] params,
			final int params_offset) {
		super.glGetNamedRenderbufferParameterivEXT(renderbuffer, pname, params, params_offset);
	}

	@Override
	public void glNamedRenderbufferStorageMultisampleEXT(final int renderbuffer, final int samples,
			final int internalformat, final int width, final int height) {
		super.glNamedRenderbufferStorageMultisampleEXT(renderbuffer, samples, internalformat, width, height);
	}

	@Override
	public void glNamedRenderbufferStorageMultisampleCoverageEXT(final int renderbuffer, final int coverageSamples,
			final int colorSamples, final int internalformat, final int width, final int height) {
		super.glNamedRenderbufferStorageMultisampleCoverageEXT(renderbuffer, coverageSamples, colorSamples,
				internalformat, width, height);
	}

	@Override
	public int glCheckNamedFramebufferStatusEXT(final int framebuffer, final int target) {
		return super.glCheckNamedFramebufferStatusEXT(framebuffer, target);
	}

	@Override
	public void glNamedFramebufferTexture1DEXT(final int framebuffer, final int attachment, final int textarget,
			final int texture, final int level) {
		super.glNamedFramebufferTexture1DEXT(framebuffer, attachment, textarget, texture, level);
	}

	@Override
	public void glNamedFramebufferTexture2DEXT(final int framebuffer, final int attachment, final int textarget,
			final int texture, final int level) {
		super.glNamedFramebufferTexture2DEXT(framebuffer, attachment, textarget, texture, level);
	}

	@Override
	public void glNamedFramebufferTexture3DEXT(final int framebuffer, final int attachment, final int textarget,
			final int texture, final int level, final int zoffset) {
		super.glNamedFramebufferTexture3DEXT(framebuffer, attachment, textarget, texture, level, zoffset);
	}

	@Override
	public void glNamedFramebufferRenderbufferEXT(final int framebuffer, final int attachment,
			final int renderbuffertarget, final int renderbuffer) {
		super.glNamedFramebufferRenderbufferEXT(framebuffer, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glGetNamedFramebufferAttachmentParameterivEXT(final int framebuffer, final int attachment,
			final int pname, final IntBuffer params) {
		super.glGetNamedFramebufferAttachmentParameterivEXT(framebuffer, attachment, pname, params);
	}

	@Override
	public void glGetNamedFramebufferAttachmentParameterivEXT(final int framebuffer, final int attachment,
			final int pname, final int[] params, final int params_offset) {
		super.glGetNamedFramebufferAttachmentParameterivEXT(framebuffer, attachment, pname, params, params_offset);
	}

	@Override
	public void glGenerateTextureMipmapEXT(final int texture, final int target) {
		super.glGenerateTextureMipmapEXT(texture, target);
	}

	@Override
	public void glGenerateMultiTexMipmapEXT(final int texunit, final int target) {
		super.glGenerateMultiTexMipmapEXT(texunit, target);
	}

	@Override
	public void glFramebufferDrawBufferEXT(final int framebuffer, final int mode) {
		super.glFramebufferDrawBufferEXT(framebuffer, mode);
	}

	@Override
	public void glFramebufferDrawBuffersEXT(final int framebuffer, final int n, final IntBuffer bufs) {
		super.glFramebufferDrawBuffersEXT(framebuffer, n, bufs);
	}

	@Override
	public void glFramebufferDrawBuffersEXT(final int framebuffer, final int n, final int[] bufs,
			final int bufs_offset) {
		super.glFramebufferDrawBuffersEXT(framebuffer, n, bufs, bufs_offset);
	}

	@Override
	public void glFramebufferReadBufferEXT(final int framebuffer, final int mode) {
		super.glFramebufferReadBufferEXT(framebuffer, mode);
	}

	@Override
	public void glGetFramebufferParameterivEXT(final int framebuffer, final int pname, final IntBuffer params) {
		super.glGetFramebufferParameterivEXT(framebuffer, pname, params);
	}

	@Override
	public void glGetFramebufferParameterivEXT(final int framebuffer, final int pname, final int[] params,
			final int params_offset) {
		super.glGetFramebufferParameterivEXT(framebuffer, pname, params, params_offset);
	}

	@Override
	public void glNamedCopyBufferSubDataEXT(final int readBuffer, final int writeBuffer, final long readOffset,
			final long writeOffset, final long size) {
		super.glNamedCopyBufferSubDataEXT(readBuffer, writeBuffer, readOffset, writeOffset, size);
	}

	@Override
	public void glNamedFramebufferTextureEXT(final int framebuffer, final int attachment, final int texture,
			final int level) {
		super.glNamedFramebufferTextureEXT(framebuffer, attachment, texture, level);
	}

	@Override
	public void glNamedFramebufferTextureLayerEXT(final int framebuffer, final int attachment, final int texture,
			final int level, final int layer) {
		super.glNamedFramebufferTextureLayerEXT(framebuffer, attachment, texture, level, layer);
	}

	@Override
	public void glNamedFramebufferTextureFaceEXT(final int framebuffer, final int attachment, final int texture,
			final int level, final int face) {
		super.glNamedFramebufferTextureFaceEXT(framebuffer, attachment, texture, level, face);
	}

	@Override
	public void glTextureRenderbufferEXT(final int texture, final int target, final int renderbuffer) {
		super.glTextureRenderbufferEXT(texture, target, renderbuffer);
	}

	@Override
	public void glMultiTexRenderbufferEXT(final int texunit, final int target, final int renderbuffer) {
		super.glMultiTexRenderbufferEXT(texunit, target, renderbuffer);
	}

	@Override
	public void glVertexArrayVertexOffsetEXT(final int vaobj, final int buffer, final int size, final int type,
			final int stride, final long offset) {
		super.glVertexArrayVertexOffsetEXT(vaobj, buffer, size, type, stride, offset);
	}

	@Override
	public void glVertexArrayColorOffsetEXT(final int vaobj, final int buffer, final int size, final int type,
			final int stride, final long offset) {
		super.glVertexArrayColorOffsetEXT(vaobj, buffer, size, type, stride, offset);
	}

	@Override
	public void glVertexArrayEdgeFlagOffsetEXT(final int vaobj, final int buffer, final int stride, final long offset) {
		super.glVertexArrayEdgeFlagOffsetEXT(vaobj, buffer, stride, offset);
	}

	@Override
	public void glVertexArrayIndexOffsetEXT(final int vaobj, final int buffer, final int type, final int stride,
			final long offset) {
		super.glVertexArrayIndexOffsetEXT(vaobj, buffer, type, stride, offset);
	}

	@Override
	public void glVertexArrayNormalOffsetEXT(final int vaobj, final int buffer, final int type, final int stride,
			final long offset) {
		super.glVertexArrayNormalOffsetEXT(vaobj, buffer, type, stride, offset);
	}

	@Override
	public void glVertexArrayTexCoordOffsetEXT(final int vaobj, final int buffer, final int size, final int type,
			final int stride, final long offset) {
		super.glVertexArrayTexCoordOffsetEXT(vaobj, buffer, size, type, stride, offset);
	}

	@Override
	public void glVertexArrayMultiTexCoordOffsetEXT(final int vaobj, final int buffer, final int texunit,
			final int size, final int type, final int stride, final long offset) {
		super.glVertexArrayMultiTexCoordOffsetEXT(vaobj, buffer, texunit, size, type, stride, offset);
	}

	@Override
	public void glVertexArrayFogCoordOffsetEXT(final int vaobj, final int buffer, final int type, final int stride,
			final long offset) {
		super.glVertexArrayFogCoordOffsetEXT(vaobj, buffer, type, stride, offset);
	}

	@Override
	public void glVertexArraySecondaryColorOffsetEXT(final int vaobj, final int buffer, final int size, final int type,
			final int stride, final long offset) {
		super.glVertexArraySecondaryColorOffsetEXT(vaobj, buffer, size, type, stride, offset);
	}

	@Override
	public void glVertexArrayVertexAttribOffsetEXT(final int vaobj, final int buffer, final int index, final int size,
			final int type, final boolean normalized, final int stride, final long offset) {
		super.glVertexArrayVertexAttribOffsetEXT(vaobj, buffer, index, size, type, normalized, stride, offset);
	}

	@Override
	public void glVertexArrayVertexAttribIOffsetEXT(final int vaobj, final int buffer, final int index, final int size,
			final int type, final int stride, final long offset) {
		super.glVertexArrayVertexAttribIOffsetEXT(vaobj, buffer, index, size, type, stride, offset);
	}

	@Override
	public void glEnableVertexArrayEXT(final int vaobj, final int array) {
		super.glEnableVertexArrayEXT(vaobj, array);
	}

	@Override
	public void glDisableVertexArrayEXT(final int vaobj, final int array) {
		super.glDisableVertexArrayEXT(vaobj, array);
	}

	@Override
	public void glEnableVertexArrayAttribEXT(final int vaobj, final int index) {
		super.glEnableVertexArrayAttribEXT(vaobj, index);
	}

	@Override
	public void glDisableVertexArrayAttribEXT(final int vaobj, final int index) {
		super.glDisableVertexArrayAttribEXT(vaobj, index);
	}

	@Override
	public void glGetVertexArrayIntegervEXT(final int vaobj, final int pname, final IntBuffer param) {
		super.glGetVertexArrayIntegervEXT(vaobj, pname, param);
	}

	@Override
	public void glGetVertexArrayIntegervEXT(final int vaobj, final int pname, final int[] param,
			final int param_offset) {
		super.glGetVertexArrayIntegervEXT(vaobj, pname, param, param_offset);
	}

	@Override
	public void glGetVertexArrayPointervEXT(final int vaobj, final int pname, final PointerBuffer param) {
		super.glGetVertexArrayPointervEXT(vaobj, pname, param);
	}

	@Override
	public void glGetVertexArrayIntegeri_vEXT(final int vaobj, final int index, final int pname,
			final IntBuffer param) {
		super.glGetVertexArrayIntegeri_vEXT(vaobj, index, pname, param);
	}

	@Override
	public void glGetVertexArrayIntegeri_vEXT(final int vaobj, final int index, final int pname, final int[] param,
			final int param_offset) {
		super.glGetVertexArrayIntegeri_vEXT(vaobj, index, pname, param, param_offset);
	}

	@Override
	public void glGetVertexArrayPointeri_vEXT(final int vaobj, final int index, final int pname,
			final PointerBuffer param) {
		super.glGetVertexArrayPointeri_vEXT(vaobj, index, pname, param);
	}

	@Override
	public void glFlushMappedNamedBufferRangeEXT(final int buffer, final long offset, final long length) {
		super.glFlushMappedNamedBufferRangeEXT(buffer, offset, length);
	}

	@Override
	public void glProgramUniform1dEXT(final int program, final int location, final double x) {
		super.glProgramUniform1dEXT(program, location, x);
	}

	@Override
	public void glProgramUniform2dEXT(final int program, final int location, final double x, final double y) {
		super.glProgramUniform2dEXT(program, location, x, y);
	}

	@Override
	public void glProgramUniform3dEXT(final int program, final int location, final double x, final double y,
			final double z) {
		super.glProgramUniform3dEXT(program, location, x, y, z);
	}

	@Override
	public void glProgramUniform4dEXT(final int program, final int location, final double x, final double y,
			final double z, final double w) {
		super.glProgramUniform4dEXT(program, location, x, y, z, w);
	}

	@Override
	public void glProgramUniform1dvEXT(final int program, final int location, final int count,
			final DoubleBuffer value) {
		super.glProgramUniform1dvEXT(program, location, count, value);
	}

	@Override
	public void glProgramUniform1dvEXT(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform1dvEXT(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform2dvEXT(final int program, final int location, final int count,
			final DoubleBuffer value) {
		super.glProgramUniform2dvEXT(program, location, count, value);
	}

	@Override
	public void glProgramUniform2dvEXT(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform2dvEXT(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform3dvEXT(final int program, final int location, final int count,
			final DoubleBuffer value) {
		super.glProgramUniform3dvEXT(program, location, count, value);
	}

	@Override
	public void glProgramUniform3dvEXT(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform3dvEXT(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniform4dvEXT(final int program, final int location, final int count,
			final DoubleBuffer value) {
		super.glProgramUniform4dvEXT(program, location, count, value);
	}

	@Override
	public void glProgramUniform4dvEXT(final int program, final int location, final int count, final double[] value,
			final int value_offset) {
		super.glProgramUniform4dvEXT(program, location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2x3dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2x3dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix2x4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix2x4dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix2x4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix2x4dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3x2dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3x2dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix3x4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix3x4dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix3x4dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix3x4dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4x2dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x2dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4x2dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glProgramUniformMatrix4x3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final DoubleBuffer value) {
		super.glProgramUniformMatrix4x3dvEXT(program, location, count, transpose, value);
	}

	@Override
	public void glProgramUniformMatrix4x3dvEXT(final int program, final int location, final int count,
			final boolean transpose, final double[] value, final int value_offset) {
		super.glProgramUniformMatrix4x3dvEXT(program, location, count, transpose, value, value_offset);
	}

	@Override
	public void glTextureBufferRangeEXT(final int texture, final int target, final int internalformat, final int buffer,
			final long offset, final long size) {
		super.glTextureBufferRangeEXT(texture, target, internalformat, buffer, offset, size);
	}

	@Override
	public void glTextureStorage2DMultisampleEXT(final int texture, final int target, final int samples,
			final int internalformat, final int width, final int height, final boolean fixedsamplelocations) {
		super.glTextureStorage2DMultisampleEXT(texture, target, samples, internalformat, width, height,
				fixedsamplelocations);
	}

	@Override
	public void glTextureStorage3DMultisampleEXT(final int texture, final int target, final int samples,
			final int internalformat, final int width, final int height, final int depth,
			final boolean fixedsamplelocations) {
		super.glTextureStorage3DMultisampleEXT(texture, target, samples, internalformat, width, height, depth,
				fixedsamplelocations);
	}

	@Override
	public void glVertexArrayBindVertexBufferEXT(final int vaobj, final int bindingindex, final int buffer,
			final long offset, final int stride) {
		super.glVertexArrayBindVertexBufferEXT(vaobj, bindingindex, buffer, offset, stride);
	}

	@Override
	public void glVertexArrayVertexAttribFormatEXT(final int vaobj, final int attribindex, final int size,
			final int type, final boolean normalized, final int relativeoffset) {
		super.glVertexArrayVertexAttribFormatEXT(vaobj, attribindex, size, type, normalized, relativeoffset);
	}

	@Override
	public void glVertexArrayVertexAttribIFormatEXT(final int vaobj, final int attribindex, final int size,
			final int type, final int relativeoffset) {
		super.glVertexArrayVertexAttribIFormatEXT(vaobj, attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexArrayVertexAttribLFormatEXT(final int vaobj, final int attribindex, final int size,
			final int type, final int relativeoffset) {
		super.glVertexArrayVertexAttribLFormatEXT(vaobj, attribindex, size, type, relativeoffset);
	}

	@Override
	public void glVertexArrayVertexAttribBindingEXT(final int vaobj, final int attribindex, final int bindingindex) {
		super.glVertexArrayVertexAttribBindingEXT(vaobj, attribindex, bindingindex);
	}

	@Override
	public void glVertexArrayVertexBindingDivisorEXT(final int vaobj, final int bindingindex, final int divisor) {
		super.glVertexArrayVertexBindingDivisorEXT(vaobj, bindingindex, divisor);
	}

	@Override
	public void glVertexArrayVertexAttribLOffsetEXT(final int vaobj, final int buffer, final int index, final int size,
			final int type, final int stride, final long offset) {
		super.glVertexArrayVertexAttribLOffsetEXT(vaobj, buffer, index, size, type, stride, offset);
	}

	@Override
	public void glTexturePageCommitmentEXT(final int texture, final int level, final int xoffset, final int yoffset,
			final int zoffset, final int width, final int height, final int depth, final boolean commit) {
		super.glTexturePageCommitmentEXT(texture, level, xoffset, yoffset, zoffset, width, height, depth, commit);
	}

	@Override
	public void glVertexArrayVertexAttribDivisorEXT(final int vaobj, final int index, final int divisor) {
		super.glVertexArrayVertexAttribDivisorEXT(vaobj, index, divisor);
	}

	@Override
	public void glColorMaskIndexed(final int index, final boolean r, final boolean g, final boolean b,
			final boolean a) {
		super.glColorMaskIndexed(index, r, g, b, a);
	}

	@Override
	public void glProgramEnvParameters4fvEXT(final int target, final int index, final int count,
			final FloatBuffer params) {
		super.glProgramEnvParameters4fvEXT(target, index, count, params);
	}

	@Override
	public void glProgramEnvParameters4fvEXT(final int target, final int index, final int count, final float[] params,
			final int params_offset) {
		super.glProgramEnvParameters4fvEXT(target, index, count, params, params_offset);
	}

	@Override
	public void glProgramLocalParameters4fvEXT(final int target, final int index, final int count,
			final FloatBuffer params) {
		super.glProgramLocalParameters4fvEXT(target, index, count, params);
	}

	@Override
	public void glProgramLocalParameters4fvEXT(final int target, final int index, final int count, final float[] params,
			final int params_offset) {
		super.glProgramLocalParameters4fvEXT(target, index, count, params, params_offset);
	}

	@Override
	public void glIndexFuncEXT(final int func, final float ref) {
		super.glIndexFuncEXT(func, ref);
	}

	@Override
	public void glIndexMaterialEXT(final int face, final int mode) {
		super.glIndexMaterialEXT(face, mode);
	}

	@Override
	public void glApplyTextureEXT(final int mode) {
		super.glApplyTextureEXT(mode);
	}

	@Override
	public void glTextureLightEXT(final int pname) {
		super.glTextureLightEXT(pname);
	}

	@Override
	public void glTextureMaterialEXT(final int face, final int mode) {
		super.glTextureMaterialEXT(face, mode);
	}

	@Override
	public void glPixelTransformParameteriEXT(final int target, final int pname, final int param) {
		super.glPixelTransformParameteriEXT(target, pname, param);
	}

	@Override
	public void glPixelTransformParameterfEXT(final int target, final int pname, final float param) {
		super.glPixelTransformParameterfEXT(target, pname, param);
	}

	@Override
	public void glPixelTransformParameterivEXT(final int target, final int pname, final IntBuffer params) {
		super.glPixelTransformParameterivEXT(target, pname, params);
	}

	@Override
	public void glPixelTransformParameterivEXT(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glPixelTransformParameterivEXT(target, pname, params, params_offset);
	}

	@Override
	public void glPixelTransformParameterfvEXT(final int target, final int pname, final FloatBuffer params) {
		super.glPixelTransformParameterfvEXT(target, pname, params);
	}

	@Override
	public void glPixelTransformParameterfvEXT(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glPixelTransformParameterfvEXT(target, pname, params, params_offset);
	}

	@Override
	public void glGetPixelTransformParameterivEXT(final int target, final int pname, final IntBuffer params) {
		super.glGetPixelTransformParameterivEXT(target, pname, params);
	}

	@Override
	public void glGetPixelTransformParameterivEXT(final int target, final int pname, final int[] params,
			final int params_offset) {
		super.glGetPixelTransformParameterivEXT(target, pname, params, params_offset);
	}

	@Override
	public void glGetPixelTransformParameterfvEXT(final int target, final int pname, final FloatBuffer params) {
		super.glGetPixelTransformParameterfvEXT(target, pname, params);
	}

	@Override
	public void glGetPixelTransformParameterfvEXT(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetPixelTransformParameterfvEXT(target, pname, params, params_offset);
	}

	@Override
	public void glPolygonOffsetClampEXT(final float factor, final float units, final float clamp) {
		super.glPolygonOffsetClampEXT(factor, units, clamp);
	}

	@Override
	public void glProvokingVertexEXT(final int mode) {
		super.glProvokingVertexEXT(mode);
	}

	@Override
	public void glRasterSamplesEXT(final int samples, final boolean fixedsamplelocations) {
		super.glRasterSamplesEXT(samples, fixedsamplelocations);
	}

	@Override
	public void glStencilClearTagEXT(final int stencilTagBits, final int stencilClearTag) {
		super.glStencilClearTagEXT(stencilTagBits, stencilClearTag);
	}

	@Override
	public void glActiveStencilFaceEXT(final int face) {
		super.glActiveStencilFaceEXT(face);
	}

	@Override
	public void glClearColorIi(final int red, final int green, final int blue, final int alpha) {
		super.glClearColorIi(red, green, blue, alpha);
	}

	@Override
	public void glClearColorIui(final int red, final int green, final int blue, final int alpha) {
		super.glClearColorIui(red, green, blue, alpha);
	}

	@Override
	public void glTextureNormalEXT(final int mode) {
		super.glTextureNormalEXT(mode);
	}

	@Override
	public void glGetQueryObjecti64vEXT(final int id, final int pname, final LongBuffer params) {
		super.glGetQueryObjecti64vEXT(id, pname, params);
	}

	@Override
	public void glGetQueryObjecti64vEXT(final int id, final int pname, final long[] params, final int params_offset) {
		super.glGetQueryObjecti64vEXT(id, pname, params, params_offset);
	}

	@Override
	public void glGetQueryObjectui64vEXT(final int id, final int pname, final LongBuffer params) {
		super.glGetQueryObjectui64vEXT(id, pname, params);
	}

	@Override
	public void glGetQueryObjectui64vEXT(final int id, final int pname, final long[] params, final int params_offset) {
		super.glGetQueryObjectui64vEXT(id, pname, params, params_offset);
	}

	@Override
	public void glBeginVertexShaderEXT() {
		super.glBeginVertexShaderEXT();
	}

	@Override
	public void glEndVertexShaderEXT() {
		super.glEndVertexShaderEXT();
	}

	@Override
	public void glBindVertexShaderEXT(final int id) {
		super.glBindVertexShaderEXT(id);
	}

	@Override
	public int glGenVertexShadersEXT(final int range) {
		return super.glGenVertexShadersEXT(range);
	}

	@Override
	public void glDeleteVertexShaderEXT(final int id) {
		super.glDeleteVertexShaderEXT(id);
	}

	@Override
	public void glShaderOp1EXT(final int op, final int res, final int arg1) {
		super.glShaderOp1EXT(op, res, arg1);
	}

	@Override
	public void glShaderOp2EXT(final int op, final int res, final int arg1, final int arg2) {
		super.glShaderOp2EXT(op, res, arg1, arg2);
	}

	@Override
	public void glShaderOp3EXT(final int op, final int res, final int arg1, final int arg2, final int arg3) {
		super.glShaderOp3EXT(op, res, arg1, arg2, arg3);
	}

	@Override
	public void glSwizzleEXT(final int res, final int in, final int outX, final int outY, final int outZ,
			final int outW) {
		super.glSwizzleEXT(res, in, outX, outY, outZ, outW);
	}

	@Override
	public void glWriteMaskEXT(final int res, final int in, final int outX, final int outY, final int outZ,
			final int outW) {
		super.glWriteMaskEXT(res, in, outX, outY, outZ, outW);
	}

	@Override
	public void glInsertComponentEXT(final int res, final int src, final int num) {
		super.glInsertComponentEXT(res, src, num);
	}

	@Override
	public void glExtractComponentEXT(final int res, final int src, final int num) {
		super.glExtractComponentEXT(res, src, num);
	}

	@Override
	public int glGenSymbolsEXT(final int datatype, final int storagetype, final int range, final int components) {
		return super.glGenSymbolsEXT(datatype, storagetype, range, components);
	}

	@Override
	public void glSetInvariantEXT(final int id, final int type, final Buffer addr) {
		super.glSetInvariantEXT(id, type, addr);
	}

	@Override
	public void glSetLocalConstantEXT(final int id, final int type, final Buffer addr) {
		super.glSetLocalConstantEXT(id, type, addr);
	}

	@Override
	public void glVariantbvEXT(final int id, final ByteBuffer addr) {
		super.glVariantbvEXT(id, addr);
	}

	@Override
	public void glVariantbvEXT(final int id, final byte[] addr, final int addr_offset) {
		super.glVariantbvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantsvEXT(final int id, final ShortBuffer addr) {
		super.glVariantsvEXT(id, addr);
	}

	@Override
	public void glVariantsvEXT(final int id, final short[] addr, final int addr_offset) {
		super.glVariantsvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantivEXT(final int id, final IntBuffer addr) {
		super.glVariantivEXT(id, addr);
	}

	@Override
	public void glVariantivEXT(final int id, final int[] addr, final int addr_offset) {
		super.glVariantivEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantfvEXT(final int id, final FloatBuffer addr) {
		super.glVariantfvEXT(id, addr);
	}

	@Override
	public void glVariantfvEXT(final int id, final float[] addr, final int addr_offset) {
		super.glVariantfvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantdvEXT(final int id, final DoubleBuffer addr) {
		super.glVariantdvEXT(id, addr);
	}

	@Override
	public void glVariantdvEXT(final int id, final double[] addr, final int addr_offset) {
		super.glVariantdvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantubvEXT(final int id, final ByteBuffer addr) {
		super.glVariantubvEXT(id, addr);
	}

	@Override
	public void glVariantubvEXT(final int id, final byte[] addr, final int addr_offset) {
		super.glVariantubvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantusvEXT(final int id, final ShortBuffer addr) {
		super.glVariantusvEXT(id, addr);
	}

	@Override
	public void glVariantusvEXT(final int id, final short[] addr, final int addr_offset) {
		super.glVariantusvEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantuivEXT(final int id, final IntBuffer addr) {
		super.glVariantuivEXT(id, addr);
	}

	@Override
	public void glVariantuivEXT(final int id, final int[] addr, final int addr_offset) {
		super.glVariantuivEXT(id, addr, addr_offset);
	}

	@Override
	public void glVariantPointerEXT(final int id, final int type, final int stride, final Buffer addr) {
		super.glVariantPointerEXT(id, type, stride, addr);
	}

	@Override
	public void glVariantPointerEXT(final int id, final int type, final int stride, final long addr_buffer_offset) {
		super.glVariantPointerEXT(id, type, stride, addr_buffer_offset);
	}

	@Override
	public void glEnableVariantClientStateEXT(final int id) {
		super.glEnableVariantClientStateEXT(id);
	}

	@Override
	public void glDisableVariantClientStateEXT(final int id) {
		super.glDisableVariantClientStateEXT(id);
	}

	@Override
	public int glBindLightParameterEXT(final int light, final int value) {
		return super.glBindLightParameterEXT(light, value);
	}

	@Override
	public int glBindMaterialParameterEXT(final int face, final int value) {
		return super.glBindMaterialParameterEXT(face, value);
	}

	@Override
	public int glBindTexGenParameterEXT(final int unit, final int coord, final int value) {
		return super.glBindTexGenParameterEXT(unit, coord, value);
	}

	@Override
	public int glBindTextureUnitParameterEXT(final int unit, final int value) {
		return super.glBindTextureUnitParameterEXT(unit, value);
	}

	@Override
	public int glBindParameterEXT(final int value) {
		return super.glBindParameterEXT(value);
	}

	@Override
	public boolean glIsVariantEnabledEXT(final int id, final int cap) {
		return super.glIsVariantEnabledEXT(id, cap);
	}

	@Override
	public void glGetVariantBooleanvEXT(final int id, final int value, final ByteBuffer data) {
		super.glGetVariantBooleanvEXT(id, value, data);
	}

	@Override
	public void glGetVariantBooleanvEXT(final int id, final int value, final byte[] data, final int data_offset) {
		super.glGetVariantBooleanvEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetVariantIntegervEXT(final int id, final int value, final IntBuffer data) {
		super.glGetVariantIntegervEXT(id, value, data);
	}

	@Override
	public void glGetVariantIntegervEXT(final int id, final int value, final int[] data, final int data_offset) {
		super.glGetVariantIntegervEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetVariantFloatvEXT(final int id, final int value, final FloatBuffer data) {
		super.glGetVariantFloatvEXT(id, value, data);
	}

	@Override
	public void glGetVariantFloatvEXT(final int id, final int value, final float[] data, final int data_offset) {
		super.glGetVariantFloatvEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetInvariantBooleanvEXT(final int id, final int value, final ByteBuffer data) {
		super.glGetInvariantBooleanvEXT(id, value, data);
	}

	@Override
	public void glGetInvariantBooleanvEXT(final int id, final int value, final byte[] data, final int data_offset) {
		super.glGetInvariantBooleanvEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetInvariantIntegervEXT(final int id, final int value, final IntBuffer data) {
		super.glGetInvariantIntegervEXT(id, value, data);
	}

	@Override
	public void glGetInvariantIntegervEXT(final int id, final int value, final int[] data, final int data_offset) {
		super.glGetInvariantIntegervEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetInvariantFloatvEXT(final int id, final int value, final FloatBuffer data) {
		super.glGetInvariantFloatvEXT(id, value, data);
	}

	@Override
	public void glGetInvariantFloatvEXT(final int id, final int value, final float[] data, final int data_offset) {
		super.glGetInvariantFloatvEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetLocalConstantBooleanvEXT(final int id, final int value, final ByteBuffer data) {
		super.glGetLocalConstantBooleanvEXT(id, value, data);
	}

	@Override
	public void glGetLocalConstantBooleanvEXT(final int id, final int value, final byte[] data, final int data_offset) {
		super.glGetLocalConstantBooleanvEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetLocalConstantIntegervEXT(final int id, final int value, final IntBuffer data) {
		super.glGetLocalConstantIntegervEXT(id, value, data);
	}

	@Override
	public void glGetLocalConstantIntegervEXT(final int id, final int value, final int[] data, final int data_offset) {
		super.glGetLocalConstantIntegervEXT(id, value, data, data_offset);
	}

	@Override
	public void glGetLocalConstantFloatvEXT(final int id, final int value, final FloatBuffer data) {
		super.glGetLocalConstantFloatvEXT(id, value, data);
	}

	@Override
	public void glGetLocalConstantFloatvEXT(final int id, final int value, final float[] data, final int data_offset) {
		super.glGetLocalConstantFloatvEXT(id, value, data, data_offset);
	}

	@Override
	public void glVertexWeightfEXT(final float weight) {
		super.glVertexWeightfEXT(weight);
	}

	@Override
	public void glVertexWeightfvEXT(final FloatBuffer weight) {
		super.glVertexWeightfvEXT(weight);
	}

	@Override
	public void glVertexWeightfvEXT(final float[] weight, final int weight_offset) {
		super.glVertexWeightfvEXT(weight, weight_offset);
	}

	@Override
	public void glVertexWeightPointerEXT(final int size, final int type, final int stride, final Buffer pointer) {
		super.glVertexWeightPointerEXT(size, type, stride, pointer);
	}

	@Override
	public void glVertexWeightPointerEXT(final int size, final int type, final int stride,
			final long pointer_buffer_offset) {
		super.glVertexWeightPointerEXT(size, type, stride, pointer_buffer_offset);
	}

	@Override
	public long glImportSyncEXT(final int external_sync_type, final long external_sync, final int flags) {
		return super.glImportSyncEXT(external_sync_type, external_sync, flags);
	}

	@Override
	public void glFrameTerminatorGREMEDY() {
		super.glFrameTerminatorGREMEDY();
	}

	@Override
	public void glStringMarkerGREMEDY(final int len, final Buffer string) {
		super.glStringMarkerGREMEDY(len, string);
	}

	@Override
	public void glBlendFuncSeparateINGR(final int sfactorRGB, final int dfactorRGB, final int sfactorAlpha,
			final int dfactorAlpha) {
		super.glBlendFuncSeparateINGR(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	@Override
	public void glApplyFramebufferAttachmentCMAAINTEL() {
		super.glApplyFramebufferAttachmentCMAAINTEL();
	}

	@Override
	public void glSyncTextureINTEL(final int texture) {
		super.glSyncTextureINTEL(texture);
	}

	@Override
	public void glUnmapTexture2DINTEL(final int texture, final int level) {
		super.glUnmapTexture2DINTEL(texture, level);
	}

	@Override
	public ByteBuffer glMapTexture2DINTEL(final int texture, final int level, final int access, final IntBuffer stride,
			final IntBuffer layout) {
		return super.glMapTexture2DINTEL(texture, level, access, stride, layout);
	}

	@Override
	public ByteBuffer glMapTexture2DINTEL(final int texture, final int level, final int access, final int[] stride,
			final int stride_offset, final int[] layout, final int layout_offset) {
		return super.glMapTexture2DINTEL(texture, level, access, stride, stride_offset, layout, layout_offset);
	}

	@Override
	public void glBeginPerfQueryINTEL(final int queryHandle) {
		super.glBeginPerfQueryINTEL(queryHandle);
	}

	@Override
	public void glCreatePerfQueryINTEL(final int queryId, final IntBuffer queryHandle) {
		super.glCreatePerfQueryINTEL(queryId, queryHandle);
	}

	@Override
	public void glCreatePerfQueryINTEL(final int queryId, final int[] queryHandle, final int queryHandle_offset) {
		super.glCreatePerfQueryINTEL(queryId, queryHandle, queryHandle_offset);
	}

	@Override
	public void glDeletePerfQueryINTEL(final int queryHandle) {
		super.glDeletePerfQueryINTEL(queryHandle);
	}

	@Override
	public void glEndPerfQueryINTEL(final int queryHandle) {
		super.glEndPerfQueryINTEL(queryHandle);
	}

	@Override
	public void glGetFirstPerfQueryIdINTEL(final IntBuffer queryId) {
		super.glGetFirstPerfQueryIdINTEL(queryId);
	}

	@Override
	public void glGetFirstPerfQueryIdINTEL(final int[] queryId, final int queryId_offset) {
		super.glGetFirstPerfQueryIdINTEL(queryId, queryId_offset);
	}

	@Override
	public void glGetNextPerfQueryIdINTEL(final int queryId, final IntBuffer nextQueryId) {
		super.glGetNextPerfQueryIdINTEL(queryId, nextQueryId);
	}

	@Override
	public void glGetNextPerfQueryIdINTEL(final int queryId, final int[] nextQueryId, final int nextQueryId_offset) {
		super.glGetNextPerfQueryIdINTEL(queryId, nextQueryId, nextQueryId_offset);
	}

	@Override
	public void glGetPerfCounterInfoINTEL(final int queryId, final int counterId, final int counterNameLength,
			final ByteBuffer counterName, final int counterDescLength, final ByteBuffer counterDesc,
			final IntBuffer counterOffset, final IntBuffer counterDataSize, final IntBuffer counterTypeEnum,
			final IntBuffer counterDataTypeEnum, final LongBuffer rawCounterMaxValue) {
		super.glGetPerfCounterInfoINTEL(queryId, counterId, counterNameLength, counterName, counterDescLength,
				counterDesc, counterOffset, counterDataSize, counterTypeEnum, counterDataTypeEnum, rawCounterMaxValue);
	}

	@Override
	public void glGetPerfCounterInfoINTEL(final int queryId, final int counterId, final int counterNameLength,
			final byte[] counterName, final int counterName_offset, final int counterDescLength,
			final byte[] counterDesc, final int counterDesc_offset, final int[] counterOffset,
			final int counterOffset_offset, final int[] counterDataSize, final int counterDataSize_offset,
			final int[] counterTypeEnum, final int counterTypeEnum_offset, final int[] counterDataTypeEnum,
			final int counterDataTypeEnum_offset, final long[] rawCounterMaxValue,
			final int rawCounterMaxValue_offset) {
		super.glGetPerfCounterInfoINTEL(queryId, counterId, counterNameLength, counterName, counterName_offset,
				counterDescLength, counterDesc, counterDesc_offset, counterOffset, counterOffset_offset,
				counterDataSize, counterDataSize_offset, counterTypeEnum, counterTypeEnum_offset, counterDataTypeEnum,
				counterDataTypeEnum_offset, rawCounterMaxValue, rawCounterMaxValue_offset);
	}

	@Override
	public void glGetPerfQueryDataINTEL(final int queryHandle, final int flags, final int dataSize, final Buffer data,
			final IntBuffer bytesWritten) {
		super.glGetPerfQueryDataINTEL(queryHandle, flags, dataSize, data, bytesWritten);
	}

	@Override
	public void glGetPerfQueryDataINTEL(final int queryHandle, final int flags, final int dataSize, final Buffer data,
			final int[] bytesWritten, final int bytesWritten_offset) {
		super.glGetPerfQueryDataINTEL(queryHandle, flags, dataSize, data, bytesWritten, bytesWritten_offset);
	}

	@Override
	public void glGetPerfQueryIdByNameINTEL(final ByteBuffer queryName, final IntBuffer queryId) {
		super.glGetPerfQueryIdByNameINTEL(queryName, queryId);
	}

	@Override
	public void glGetPerfQueryIdByNameINTEL(final byte[] queryName, final int queryName_offset, final int[] queryId,
			final int queryId_offset) {
		super.glGetPerfQueryIdByNameINTEL(queryName, queryName_offset, queryId, queryId_offset);
	}

	@Override
	public void glGetPerfQueryInfoINTEL(final int queryId, final int queryNameLength, final ByteBuffer queryName,
			final IntBuffer dataSize, final IntBuffer noCounters, final IntBuffer noInstances,
			final IntBuffer capsMask) {
		super.glGetPerfQueryInfoINTEL(queryId, queryNameLength, queryName, dataSize, noCounters, noInstances, capsMask);
	}

	@Override
	public void glGetPerfQueryInfoINTEL(final int queryId, final int queryNameLength, final byte[] queryName,
			final int queryName_offset, final int[] dataSize, final int dataSize_offset, final int[] noCounters,
			final int noCounters_offset, final int[] noInstances, final int noInstances_offset, final int[] capsMask,
			final int capsMask_offset) {
		super.glGetPerfQueryInfoINTEL(queryId, queryNameLength, queryName, queryName_offset, dataSize, dataSize_offset,
				noCounters, noCounters_offset, noInstances, noInstances_offset, capsMask, capsMask_offset);
	}

	@Override
	public void glBeginConditionalRenderNVX(final int id) {
		super.glBeginConditionalRenderNVX(id);
	}

	@Override
	public void glEndConditionalRenderNVX() {
		super.glEndConditionalRenderNVX();
	}

	@Override
	public void glMultiDrawArraysIndirectBindlessNV(final int mode, final Buffer indirect, final int drawCount,
			final int stride, final int vertexBufferCount) {
		super.glMultiDrawArraysIndirectBindlessNV(mode, indirect, drawCount, stride, vertexBufferCount);
	}

	@Override
	public void glMultiDrawElementsIndirectBindlessNV(final int mode, final int type, final Buffer indirect,
			final int drawCount, final int stride, final int vertexBufferCount) {
		super.glMultiDrawElementsIndirectBindlessNV(mode, type, indirect, drawCount, stride, vertexBufferCount);
	}

	@Override
	public void glMultiDrawArraysIndirectBindlessCountNV(final int mode, final Buffer indirect, final int drawCount,
			final int maxDrawCount, final int stride, final int vertexBufferCount) {
		super.glMultiDrawArraysIndirectBindlessCountNV(mode, indirect, drawCount, maxDrawCount, stride,
				vertexBufferCount);
	}

	@Override
	public void glMultiDrawElementsIndirectBindlessCountNV(final int mode, final int type, final Buffer indirect,
			final int drawCount, final int maxDrawCount, final int stride, final int vertexBufferCount) {
		super.glMultiDrawElementsIndirectBindlessCountNV(mode, type, indirect, drawCount, maxDrawCount, stride,
				vertexBufferCount);
	}

	@Override
	public void glCreateStatesNV(final int n, final IntBuffer states) {
		super.glCreateStatesNV(n, states);
	}

	@Override
	public void glCreateStatesNV(final int n, final int[] states, final int states_offset) {
		super.glCreateStatesNV(n, states, states_offset);
	}

	@Override
	public void glDeleteStatesNV(final int n, final IntBuffer states) {
		super.glDeleteStatesNV(n, states);
	}

	@Override
	public void glDeleteStatesNV(final int n, final int[] states, final int states_offset) {
		super.glDeleteStatesNV(n, states, states_offset);
	}

	@Override
	public boolean glIsStateNV(final int state) {
		return super.glIsStateNV(state);
	}

	@Override
	public void glStateCaptureNV(final int state, final int mode) {
		super.glStateCaptureNV(state, mode);
	}

	@Override
	public int glGetCommandHeaderNV(final int tokenID, final int size) {
		return super.glGetCommandHeaderNV(tokenID, size);
	}

	@Override
	public short glGetStageIndexNV(final int shadertype) {
		return super.glGetStageIndexNV(shadertype);
	}

	@Override
	public void glDrawCommandsNV(final int primitiveMode, final int buffer, final PointerBuffer indirects,
			final IntBuffer sizes, final int count) {
		super.glDrawCommandsNV(primitiveMode, buffer, indirects, sizes, count);
	}

	@Override
	public void glDrawCommandsNV(final int primitiveMode, final int buffer, final PointerBuffer indirects,
			final int[] sizes, final int sizes_offset, final int count) {
		super.glDrawCommandsNV(primitiveMode, buffer, indirects, sizes, sizes_offset, count);
	}

	@Override
	public void glDrawCommandsAddressNV(final int primitiveMode, final LongBuffer indirects, final IntBuffer sizes,
			final int count) {
		super.glDrawCommandsAddressNV(primitiveMode, indirects, sizes, count);
	}

	@Override
	public void glDrawCommandsAddressNV(final int primitiveMode, final long[] indirects, final int indirects_offset,
			final int[] sizes, final int sizes_offset, final int count) {
		super.glDrawCommandsAddressNV(primitiveMode, indirects, indirects_offset, sizes, sizes_offset, count);
	}

	@Override
	public void glDrawCommandsStatesNV(final int buffer, final PointerBuffer indirects, final IntBuffer sizes,
			final IntBuffer states, final IntBuffer fbos, final int count) {
		super.glDrawCommandsStatesNV(buffer, indirects, sizes, states, fbos, count);
	}

	@Override
	public void glDrawCommandsStatesNV(final int buffer, final PointerBuffer indirects, final int[] sizes,
			final int sizes_offset, final int[] states, final int states_offset, final int[] fbos,
			final int fbos_offset, final int count) {
		super.glDrawCommandsStatesNV(buffer, indirects, sizes, sizes_offset, states, states_offset, fbos, fbos_offset,
				count);
	}

	@Override
	public void glDrawCommandsStatesAddressNV(final LongBuffer indirects, final IntBuffer sizes, final IntBuffer states,
			final IntBuffer fbos, final int count) {
		super.glDrawCommandsStatesAddressNV(indirects, sizes, states, fbos, count);
	}

	@Override
	public void glDrawCommandsStatesAddressNV(final long[] indirects, final int indirects_offset, final int[] sizes,
			final int sizes_offset, final int[] states, final int states_offset, final int[] fbos,
			final int fbos_offset, final int count) {
		super.glDrawCommandsStatesAddressNV(indirects, indirects_offset, sizes, sizes_offset, states, states_offset,
				fbos, fbos_offset, count);
	}

	@Override
	public void glCreateCommandListsNV(final int n, final IntBuffer lists) {
		super.glCreateCommandListsNV(n, lists);
	}

	@Override
	public void glCreateCommandListsNV(final int n, final int[] lists, final int lists_offset) {
		super.glCreateCommandListsNV(n, lists, lists_offset);
	}

	@Override
	public void glDeleteCommandListsNV(final int n, final IntBuffer lists) {
		super.glDeleteCommandListsNV(n, lists);
	}

	@Override
	public void glDeleteCommandListsNV(final int n, final int[] lists, final int lists_offset) {
		super.glDeleteCommandListsNV(n, lists, lists_offset);
	}

	@Override
	public boolean glIsCommandListNV(final int list) {
		return super.glIsCommandListNV(list);
	}

	@Override
	public void glListDrawCommandsStatesClientNV(final int list, final int segment, final PointerBuffer indirects,
			final IntBuffer sizes, final IntBuffer states, final IntBuffer fbos, final int count) {
		super.glListDrawCommandsStatesClientNV(list, segment, indirects, sizes, states, fbos, count);
	}

	@Override
	public void glListDrawCommandsStatesClientNV(final int list, final int segment, final PointerBuffer indirects,
			final int[] sizes, final int sizes_offset, final int[] states, final int states_offset, final int[] fbos,
			final int fbos_offset, final int count) {
		super.glListDrawCommandsStatesClientNV(list, segment, indirects, sizes, sizes_offset, states, states_offset,
				fbos, fbos_offset, count);
	}

	@Override
	public void glCommandListSegmentsNV(final int list, final int segments) {
		super.glCommandListSegmentsNV(list, segments);
	}

	@Override
	public void glCompileCommandListNV(final int list) {
		super.glCompileCommandListNV(list);
	}

	@Override
	public void glCallCommandListNV(final int list) {
		super.glCallCommandListNV(list);
	}

	@Override
	public void glSubpixelPrecisionBiasNV(final int xbits, final int ybits) {
		super.glSubpixelPrecisionBiasNV(xbits, ybits);
	}

	@Override
	public void glConservativeRasterParameterfNV(final int pname, final float value) {
		super.glConservativeRasterParameterfNV(pname, value);
	}

	@Override
	public void glCopyImageSubDataNV(final int srcName, final int srcTarget, final int srcLevel, final int srcX,
			final int srcY, final int srcZ, final int dstName, final int dstTarget, final int dstLevel, final int dstX,
			final int dstY, final int dstZ, final int width, final int height, final int depth) {
		super.glCopyImageSubDataNV(srcName, srcTarget, srcLevel, srcX, srcY, srcZ, dstName, dstTarget, dstLevel, dstX,
				dstY, dstZ, width, height, depth);
	}

	@Override
	public void glDrawTextureNV(final int texture, final int sampler, final float x0, final float y0, final float x1,
			final float y1, final float z, final float s0, final float t0, final float s1, final float t1) {
		super.glDrawTextureNV(texture, sampler, x0, y0, x1, y1, z, s0, t0, s1, t1);
	}

	@Override
	public void glMapControlPointsNV(final int target, final int index, final int type, final int ustride,
			final int vstride, final int uorder, final int vorder, final boolean packed, final Buffer points) {
		super.glMapControlPointsNV(target, index, type, ustride, vstride, uorder, vorder, packed, points);
	}

	@Override
	public void glMapParameterivNV(final int target, final int pname, final IntBuffer params) {
		super.glMapParameterivNV(target, pname, params);
	}

	@Override
	public void glMapParameterivNV(final int target, final int pname, final int[] params, final int params_offset) {
		super.glMapParameterivNV(target, pname, params, params_offset);
	}

	@Override
	public void glMapParameterfvNV(final int target, final int pname, final FloatBuffer params) {
		super.glMapParameterfvNV(target, pname, params);
	}

	@Override
	public void glMapParameterfvNV(final int target, final int pname, final float[] params, final int params_offset) {
		super.glMapParameterfvNV(target, pname, params, params_offset);
	}

	@Override
	public void glGetMapControlPointsNV(final int target, final int index, final int type, final int ustride,
			final int vstride, final boolean packed, final Buffer points) {
		super.glGetMapControlPointsNV(target, index, type, ustride, vstride, packed, points);
	}

	@Override
	public void glGetMapParameterivNV(final int target, final int pname, final IntBuffer params) {
		super.glGetMapParameterivNV(target, pname, params);
	}

	@Override
	public void glGetMapParameterivNV(final int target, final int pname, final int[] params, final int params_offset) {
		super.glGetMapParameterivNV(target, pname, params, params_offset);
	}

	@Override
	public void glGetMapParameterfvNV(final int target, final int pname, final FloatBuffer params) {
		super.glGetMapParameterfvNV(target, pname, params);
	}

	@Override
	public void glGetMapParameterfvNV(final int target, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMapParameterfvNV(target, pname, params, params_offset);
	}

	@Override
	public void glGetMapAttribParameterivNV(final int target, final int index, final int pname,
			final IntBuffer params) {
		super.glGetMapAttribParameterivNV(target, index, pname, params);
	}

	@Override
	public void glGetMapAttribParameterivNV(final int target, final int index, final int pname, final int[] params,
			final int params_offset) {
		super.glGetMapAttribParameterivNV(target, index, pname, params, params_offset);
	}

	@Override
	public void glGetMapAttribParameterfvNV(final int target, final int index, final int pname,
			final FloatBuffer params) {
		super.glGetMapAttribParameterfvNV(target, index, pname, params);
	}

	@Override
	public void glGetMapAttribParameterfvNV(final int target, final int index, final int pname, final float[] params,
			final int params_offset) {
		super.glGetMapAttribParameterfvNV(target, index, pname, params, params_offset);
	}

	@Override
	public void glEvalMapsNV(final int target, final int mode) {
		super.glEvalMapsNV(target, mode);
	}

	@Override
	public void glGetMultisamplefvNV(final int pname, final int index, final FloatBuffer val) {
		super.glGetMultisamplefvNV(pname, index, val);
	}

	@Override
	public void glGetMultisamplefvNV(final int pname, final int index, final float[] val, final int val_offset) {
		super.glGetMultisamplefvNV(pname, index, val, val_offset);
	}

	@Override
	public void glSampleMaskIndexedNV(final int index, final int mask) {
		super.glSampleMaskIndexedNV(index, mask);
	}

	@Override
	public void glTexRenderbufferNV(final int target, final int renderbuffer) {
		super.glTexRenderbufferNV(target, renderbuffer);
	}

	@Override
	public void glFragmentCoverageColorNV(final int color) {
		super.glFragmentCoverageColorNV(color);
	}

	@Override
	public void glCoverageModulationTableNV(final int n, final FloatBuffer v) {
		super.glCoverageModulationTableNV(n, v);
	}

	@Override
	public void glCoverageModulationTableNV(final int n, final float[] v, final int v_offset) {
		super.glCoverageModulationTableNV(n, v, v_offset);
	}

	@Override
	public void glGetCoverageModulationTableNV(final int bufsize, final FloatBuffer v) {
		super.glGetCoverageModulationTableNV(bufsize, v);
	}

	@Override
	public void glGetCoverageModulationTableNV(final int bufsize, final float[] v, final int v_offset) {
		super.glGetCoverageModulationTableNV(bufsize, v, v_offset);
	}

	@Override
	public void glCoverageModulationNV(final int components) {
		super.glCoverageModulationNV(components);
	}

	@Override
	public void glRenderbufferStorageMultisampleCoverageNV(final int target, final int coverageSamples,
			final int colorSamples, final int internalformat, final int width, final int height) {
		super.glRenderbufferStorageMultisampleCoverageNV(target, coverageSamples, colorSamples, internalformat, width,
				height);
	}

	@Override
	public void glProgramVertexLimitNV(final int target, final int limit) {
		super.glProgramVertexLimitNV(target, limit);
	}

	@Override
	public void glFramebufferTextureEXT(final int target, final int attachment, final int texture, final int level) {
		super.glFramebufferTextureEXT(target, attachment, texture, level);
	}

	@Override
	public void glFramebufferTextureFaceEXT(final int target, final int attachment, final int texture, final int level,
			final int face) {
		super.glFramebufferTextureFaceEXT(target, attachment, texture, level, face);
	}

	@Override
	public void glProgramLocalParameterI4iNV(final int target, final int index, final int x, final int y, final int z,
			final int w) {
		super.glProgramLocalParameterI4iNV(target, index, x, y, z, w);
	}

	@Override
	public void glProgramLocalParameterI4ivNV(final int target, final int index, final IntBuffer params) {
		super.glProgramLocalParameterI4ivNV(target, index, params);
	}

	@Override
	public void glProgramLocalParameterI4ivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glProgramLocalParameterI4ivNV(target, index, params, params_offset);
	}

	@Override
	public void glProgramLocalParametersI4ivNV(final int target, final int index, final int count,
			final IntBuffer params) {
		super.glProgramLocalParametersI4ivNV(target, index, count, params);
	}

	@Override
	public void glProgramLocalParametersI4ivNV(final int target, final int index, final int count, final int[] params,
			final int params_offset) {
		super.glProgramLocalParametersI4ivNV(target, index, count, params, params_offset);
	}

	@Override
	public void glProgramLocalParameterI4uiNV(final int target, final int index, final int x, final int y, final int z,
			final int w) {
		super.glProgramLocalParameterI4uiNV(target, index, x, y, z, w);
	}

	@Override
	public void glProgramLocalParameterI4uivNV(final int target, final int index, final IntBuffer params) {
		super.glProgramLocalParameterI4uivNV(target, index, params);
	}

	@Override
	public void glProgramLocalParameterI4uivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glProgramLocalParameterI4uivNV(target, index, params, params_offset);
	}

	@Override
	public void glProgramLocalParametersI4uivNV(final int target, final int index, final int count,
			final IntBuffer params) {
		super.glProgramLocalParametersI4uivNV(target, index, count, params);
	}

	@Override
	public void glProgramLocalParametersI4uivNV(final int target, final int index, final int count, final int[] params,
			final int params_offset) {
		super.glProgramLocalParametersI4uivNV(target, index, count, params, params_offset);
	}

	@Override
	public void glProgramEnvParameterI4iNV(final int target, final int index, final int x, final int y, final int z,
			final int w) {
		super.glProgramEnvParameterI4iNV(target, index, x, y, z, w);
	}

	@Override
	public void glProgramEnvParameterI4ivNV(final int target, final int index, final IntBuffer params) {
		super.glProgramEnvParameterI4ivNV(target, index, params);
	}

	@Override
	public void glProgramEnvParameterI4ivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glProgramEnvParameterI4ivNV(target, index, params, params_offset);
	}

	@Override
	public void glProgramEnvParametersI4ivNV(final int target, final int index, final int count,
			final IntBuffer params) {
		super.glProgramEnvParametersI4ivNV(target, index, count, params);
	}

	@Override
	public void glProgramEnvParametersI4ivNV(final int target, final int index, final int count, final int[] params,
			final int params_offset) {
		super.glProgramEnvParametersI4ivNV(target, index, count, params, params_offset);
	}

	@Override
	public void glProgramEnvParameterI4uiNV(final int target, final int index, final int x, final int y, final int z,
			final int w) {
		super.glProgramEnvParameterI4uiNV(target, index, x, y, z, w);
	}

	@Override
	public void glProgramEnvParameterI4uivNV(final int target, final int index, final IntBuffer params) {
		super.glProgramEnvParameterI4uivNV(target, index, params);
	}

	@Override
	public void glProgramEnvParameterI4uivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glProgramEnvParameterI4uivNV(target, index, params, params_offset);
	}

	@Override
	public void glProgramEnvParametersI4uivNV(final int target, final int index, final int count,
			final IntBuffer params) {
		super.glProgramEnvParametersI4uivNV(target, index, count, params);
	}

	@Override
	public void glProgramEnvParametersI4uivNV(final int target, final int index, final int count, final int[] params,
			final int params_offset) {
		super.glProgramEnvParametersI4uivNV(target, index, count, params, params_offset);
	}

	@Override
	public void glGetProgramLocalParameterIivNV(final int target, final int index, final IntBuffer params) {
		super.glGetProgramLocalParameterIivNV(target, index, params);
	}

	@Override
	public void glGetProgramLocalParameterIivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glGetProgramLocalParameterIivNV(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramLocalParameterIuivNV(final int target, final int index, final IntBuffer params) {
		super.glGetProgramLocalParameterIuivNV(target, index, params);
	}

	@Override
	public void glGetProgramLocalParameterIuivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glGetProgramLocalParameterIuivNV(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramEnvParameterIivNV(final int target, final int index, final IntBuffer params) {
		super.glGetProgramEnvParameterIivNV(target, index, params);
	}

	@Override
	public void glGetProgramEnvParameterIivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glGetProgramEnvParameterIivNV(target, index, params, params_offset);
	}

	@Override
	public void glGetProgramEnvParameterIuivNV(final int target, final int index, final IntBuffer params) {
		super.glGetProgramEnvParameterIuivNV(target, index, params);
	}

	@Override
	public void glGetProgramEnvParameterIuivNV(final int target, final int index, final int[] params,
			final int params_offset) {
		super.glGetProgramEnvParameterIuivNV(target, index, params, params_offset);
	}

	@Override
	public void glProgramSubroutineParametersuivNV(final int target, final int count, final IntBuffer params) {
		super.glProgramSubroutineParametersuivNV(target, count, params);
	}

	@Override
	public void glProgramSubroutineParametersuivNV(final int target, final int count, final int[] params,
			final int params_offset) {
		super.glProgramSubroutineParametersuivNV(target, count, params, params_offset);
	}

	@Override
	public void glGetProgramSubroutineParameteruivNV(final int target, final int index, final IntBuffer param) {
		super.glGetProgramSubroutineParameteruivNV(target, index, param);
	}

	@Override
	public void glGetProgramSubroutineParameteruivNV(final int target, final int index, final int[] param,
			final int param_offset) {
		super.glGetProgramSubroutineParameteruivNV(target, index, param, param_offset);
	}

	@Override
	public void glVertex2h(final short x, final short y) {
		super.glVertex2h(x, y);
	}

	@Override
	public void glVertex2hv(final ShortBuffer v) {
		super.glVertex2hv(v);
	}

	@Override
	public void glVertex2hv(final short[] v, final int v_offset) {
		super.glVertex2hv(v, v_offset);
	}

	@Override
	public void glVertex3h(final short x, final short y, final short z) {
		super.glVertex3h(x, y, z);
	}

	@Override
	public void glVertex3hv(final ShortBuffer v) {
		super.glVertex3hv(v);
	}

	@Override
	public void glVertex3hv(final short[] v, final int v_offset) {
		super.glVertex3hv(v, v_offset);
	}

	@Override
	public void glVertex4h(final short x, final short y, final short z, final short w) {
		super.glVertex4h(x, y, z, w);
	}

	@Override
	public void glVertex4hv(final ShortBuffer v) {
		super.glVertex4hv(v);
	}

	@Override
	public void glVertex4hv(final short[] v, final int v_offset) {
		super.glVertex4hv(v, v_offset);
	}

	@Override
	public void glNormal3h(final short nx, final short ny, final short nz) {
		super.glNormal3h(nx, ny, nz);
	}

	@Override
	public void glNormal3hv(final ShortBuffer v) {
		super.glNormal3hv(v);
	}

	@Override
	public void glNormal3hv(final short[] v, final int v_offset) {
		super.glNormal3hv(v, v_offset);
	}

	@Override
	public void glColor3h(final short red, final short green, final short blue) {
		super.glColor3h(red, green, blue);
	}

	@Override
	public void glColor3hv(final ShortBuffer v) {
		super.glColor3hv(v);
	}

	@Override
	public void glColor3hv(final short[] v, final int v_offset) {
		super.glColor3hv(v, v_offset);
	}

	@Override
	public void glColor4h(final short red, final short green, final short blue, final short alpha) {
		super.glColor4h(red, green, blue, alpha);
	}

	@Override
	public void glColor4hv(final ShortBuffer v) {
		super.glColor4hv(v);
	}

	@Override
	public void glColor4hv(final short[] v, final int v_offset) {
		super.glColor4hv(v, v_offset);
	}

	@Override
	public void glTexCoord1h(final short s) {
		super.glTexCoord1h(s);
	}

	@Override
	public void glTexCoord1hv(final ShortBuffer v) {
		super.glTexCoord1hv(v);
	}

	@Override
	public void glTexCoord1hv(final short[] v, final int v_offset) {
		super.glTexCoord1hv(v, v_offset);
	}

	@Override
	public void glTexCoord2h(final short s, final short t) {
		super.glTexCoord2h(s, t);
	}

	@Override
	public void glTexCoord2hv(final ShortBuffer v) {
		super.glTexCoord2hv(v);
	}

	@Override
	public void glTexCoord2hv(final short[] v, final int v_offset) {
		super.glTexCoord2hv(v, v_offset);
	}

	@Override
	public void glTexCoord3h(final short s, final short t, final short r) {
		super.glTexCoord3h(s, t, r);
	}

	@Override
	public void glTexCoord3hv(final ShortBuffer v) {
		super.glTexCoord3hv(v);
	}

	@Override
	public void glTexCoord3hv(final short[] v, final int v_offset) {
		super.glTexCoord3hv(v, v_offset);
	}

	@Override
	public void glTexCoord4h(final short s, final short t, final short r, final short q) {
		super.glTexCoord4h(s, t, r, q);
	}

	@Override
	public void glTexCoord4hv(final ShortBuffer v) {
		super.glTexCoord4hv(v);
	}

	@Override
	public void glTexCoord4hv(final short[] v, final int v_offset) {
		super.glTexCoord4hv(v, v_offset);
	}

	@Override
	public void glMultiTexCoord1h(final int target, final short s) {
		super.glMultiTexCoord1h(target, s);
	}

	@Override
	public void glMultiTexCoord1hv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord1hv(target, v);
	}

	@Override
	public void glMultiTexCoord1hv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord1hv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord2h(final int target, final short s, final short t) {
		super.glMultiTexCoord2h(target, s, t);
	}

	@Override
	public void glMultiTexCoord2hv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord2hv(target, v);
	}

	@Override
	public void glMultiTexCoord2hv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord2hv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord3h(final int target, final short s, final short t, final short r) {
		super.glMultiTexCoord3h(target, s, t, r);
	}

	@Override
	public void glMultiTexCoord3hv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord3hv(target, v);
	}

	@Override
	public void glMultiTexCoord3hv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord3hv(target, v, v_offset);
	}

	@Override
	public void glMultiTexCoord4h(final int target, final short s, final short t, final short r, final short q) {
		super.glMultiTexCoord4h(target, s, t, r, q);
	}

	@Override
	public void glMultiTexCoord4hv(final int target, final ShortBuffer v) {
		super.glMultiTexCoord4hv(target, v);
	}

	@Override
	public void glMultiTexCoord4hv(final int target, final short[] v, final int v_offset) {
		super.glMultiTexCoord4hv(target, v, v_offset);
	}

	@Override
	public void glFogCoordh(final short fog) {
		super.glFogCoordh(fog);
	}

	@Override
	public void glFogCoordhv(final ShortBuffer fog) {
		super.glFogCoordhv(fog);
	}

	@Override
	public void glFogCoordhv(final short[] fog, final int fog_offset) {
		super.glFogCoordhv(fog, fog_offset);
	}

	@Override
	public void glSecondaryColor3h(final short red, final short green, final short blue) {
		super.glSecondaryColor3h(red, green, blue);
	}

	@Override
	public void glSecondaryColor3hv(final ShortBuffer v) {
		super.glSecondaryColor3hv(v);
	}

	@Override
	public void glSecondaryColor3hv(final short[] v, final int v_offset) {
		super.glSecondaryColor3hv(v, v_offset);
	}

	@Override
	public void glVertexWeighth(final short weight) {
		super.glVertexWeighth(weight);
	}

	@Override
	public void glVertexWeighthv(final ShortBuffer weight) {
		super.glVertexWeighthv(weight);
	}

	@Override
	public void glVertexWeighthv(final short[] weight, final int weight_offset) {
		super.glVertexWeighthv(weight, weight_offset);
	}

	@Override
	public void glVertexAttrib1h(final int index, final short x) {
		super.glVertexAttrib1h(index, x);
	}

	@Override
	public void glVertexAttrib1hv(final int index, final ShortBuffer v) {
		super.glVertexAttrib1hv(index, v);
	}

	@Override
	public void glVertexAttrib1hv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib1hv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib2h(final int index, final short x, final short y) {
		super.glVertexAttrib2h(index, x, y);
	}

	@Override
	public void glVertexAttrib2hv(final int index, final ShortBuffer v) {
		super.glVertexAttrib2hv(index, v);
	}

	@Override
	public void glVertexAttrib2hv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib2hv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib3h(final int index, final short x, final short y, final short z) {
		super.glVertexAttrib3h(index, x, y, z);
	}

	@Override
	public void glVertexAttrib3hv(final int index, final ShortBuffer v) {
		super.glVertexAttrib3hv(index, v);
	}

	@Override
	public void glVertexAttrib3hv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib3hv(index, v, v_offset);
	}

	@Override
	public void glVertexAttrib4h(final int index, final short x, final short y, final short z, final short w) {
		super.glVertexAttrib4h(index, x, y, z, w);
	}

	@Override
	public void glVertexAttrib4hv(final int index, final ShortBuffer v) {
		super.glVertexAttrib4hv(index, v);
	}

	@Override
	public void glVertexAttrib4hv(final int index, final short[] v, final int v_offset) {
		super.glVertexAttrib4hv(index, v, v_offset);
	}

	@Override
	public void glVertexAttribs1hv(final int index, final int n, final ShortBuffer v) {
		super.glVertexAttribs1hv(index, n, v);
	}

	@Override
	public void glVertexAttribs1hv(final int index, final int n, final short[] v, final int v_offset) {
		super.glVertexAttribs1hv(index, n, v, v_offset);
	}

	@Override
	public void glVertexAttribs2hv(final int index, final int n, final ShortBuffer v) {
		super.glVertexAttribs2hv(index, n, v);
	}

	@Override
	public void glVertexAttribs2hv(final int index, final int n, final short[] v, final int v_offset) {
		super.glVertexAttribs2hv(index, n, v, v_offset);
	}

	@Override
	public void glVertexAttribs3hv(final int index, final int n, final ShortBuffer v) {
		super.glVertexAttribs3hv(index, n, v);
	}

	@Override
	public void glVertexAttribs3hv(final int index, final int n, final short[] v, final int v_offset) {
		super.glVertexAttribs3hv(index, n, v, v_offset);
	}

	@Override
	public void glVertexAttribs4hv(final int index, final int n, final ShortBuffer v) {
		super.glVertexAttribs4hv(index, n, v);
	}

	@Override
	public void glVertexAttribs4hv(final int index, final int n, final short[] v, final int v_offset) {
		super.glVertexAttribs4hv(index, n, v, v_offset);
	}

	@Override
	public void glGenOcclusionQueriesNV(final int n, final IntBuffer ids) {
		super.glGenOcclusionQueriesNV(n, ids);
	}

	@Override
	public void glGenOcclusionQueriesNV(final int n, final int[] ids, final int ids_offset) {
		super.glGenOcclusionQueriesNV(n, ids, ids_offset);
	}

	@Override
	public void glDeleteOcclusionQueriesNV(final int n, final IntBuffer ids) {
		super.glDeleteOcclusionQueriesNV(n, ids);
	}

	@Override
	public void glDeleteOcclusionQueriesNV(final int n, final int[] ids, final int ids_offset) {
		super.glDeleteOcclusionQueriesNV(n, ids, ids_offset);
	}

	@Override
	public boolean glIsOcclusionQueryNV(final int id) {
		return super.glIsOcclusionQueryNV(id);
	}

	@Override
	public void glBeginOcclusionQueryNV(final int id) {
		super.glBeginOcclusionQueryNV(id);
	}

	@Override
	public void glEndOcclusionQueryNV() {
		super.glEndOcclusionQueryNV();
	}

	@Override
	public void glGetOcclusionQueryivNV(final int id, final int pname, final IntBuffer params) {
		super.glGetOcclusionQueryivNV(id, pname, params);
	}

	@Override
	public void glGetOcclusionQueryivNV(final int id, final int pname, final int[] params, final int params_offset) {
		super.glGetOcclusionQueryivNV(id, pname, params, params_offset);
	}

	@Override
	public void glGetOcclusionQueryuivNV(final int id, final int pname, final IntBuffer params) {
		super.glGetOcclusionQueryuivNV(id, pname, params);
	}

	@Override
	public void glGetOcclusionQueryuivNV(final int id, final int pname, final int[] params, final int params_offset) {
		super.glGetOcclusionQueryuivNV(id, pname, params, params_offset);
	}

	@Override
	public void glProgramBufferParametersfvNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final FloatBuffer params) {
		super.glProgramBufferParametersfvNV(target, bindingIndex, wordIndex, count, params);
	}

	@Override
	public void glProgramBufferParametersfvNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final float[] params, final int params_offset) {
		super.glProgramBufferParametersfvNV(target, bindingIndex, wordIndex, count, params, params_offset);
	}

	@Override
	public void glProgramBufferParametersIivNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final IntBuffer params) {
		super.glProgramBufferParametersIivNV(target, bindingIndex, wordIndex, count, params);
	}

	@Override
	public void glProgramBufferParametersIivNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final int[] params, final int params_offset) {
		super.glProgramBufferParametersIivNV(target, bindingIndex, wordIndex, count, params, params_offset);
	}

	@Override
	public void glProgramBufferParametersIuivNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final IntBuffer params) {
		super.glProgramBufferParametersIuivNV(target, bindingIndex, wordIndex, count, params);
	}

	@Override
	public void glProgramBufferParametersIuivNV(final int target, final int bindingIndex, final int wordIndex,
			final int count, final int[] params, final int params_offset) {
		super.glProgramBufferParametersIuivNV(target, bindingIndex, wordIndex, count, params, params_offset);
	}

	@Override
	public void glPixelDataRangeNV(final int target, final int length, final Buffer pointer) {
		super.glPixelDataRangeNV(target, length, pointer);
	}

	@Override
	public void glFlushPixelDataRangeNV(final int target) {
		super.glFlushPixelDataRangeNV(target);
	}

	@Override
	public void glPrimitiveRestartNV() {
		super.glPrimitiveRestartNV();
	}

	@Override
	public void glPrimitiveRestartIndexNV(final int index) {
		super.glPrimitiveRestartIndexNV(index);
	}

	@Override
	public void glFramebufferSampleLocationsfvNV(final int target, final int start, final int count,
			final FloatBuffer v) {
		super.glFramebufferSampleLocationsfvNV(target, start, count, v);
	}

	@Override
	public void glFramebufferSampleLocationsfvNV(final int target, final int start, final int count, final float[] v,
			final int v_offset) {
		super.glFramebufferSampleLocationsfvNV(target, start, count, v, v_offset);
	}

	@Override
	public void glNamedFramebufferSampleLocationsfvNV(final int framebuffer, final int start, final int count,
			final FloatBuffer v) {
		super.glNamedFramebufferSampleLocationsfvNV(framebuffer, start, count, v);
	}

	@Override
	public void glNamedFramebufferSampleLocationsfvNV(final int framebuffer, final int start, final int count,
			final float[] v, final int v_offset) {
		super.glNamedFramebufferSampleLocationsfvNV(framebuffer, start, count, v, v_offset);
	}

	@Override
	public void glResolveDepthValuesNV() {
		super.glResolveDepthValuesNV();
	}

	@Override
	public void glMakeBufferResidentNV(final int target, final int access) {
		super.glMakeBufferResidentNV(target, access);
	}

	@Override
	public void glMakeBufferNonResidentNV(final int target) {
		super.glMakeBufferNonResidentNV(target);
	}

	@Override
	public boolean glIsBufferResidentNV(final int target) {
		return super.glIsBufferResidentNV(target);
	}

	@Override
	public void glMakeNamedBufferResidentNV(final int buffer, final int access) {
		super.glMakeNamedBufferResidentNV(buffer, access);
	}

	@Override
	public void glMakeNamedBufferNonResidentNV(final int buffer) {
		super.glMakeNamedBufferNonResidentNV(buffer);
	}

	@Override
	public boolean glIsNamedBufferResidentNV(final int buffer) {
		return super.glIsNamedBufferResidentNV(buffer);
	}

	@Override
	public void glGetBufferParameterui64vNV(final int target, final int pname, final LongBuffer params) {
		super.glGetBufferParameterui64vNV(target, pname, params);
	}

	@Override
	public void glGetBufferParameterui64vNV(final int target, final int pname, final long[] params,
			final int params_offset) {
		super.glGetBufferParameterui64vNV(target, pname, params, params_offset);
	}

	@Override
	public void glGetNamedBufferParameterui64vNV(final int buffer, final int pname, final LongBuffer params) {
		super.glGetNamedBufferParameterui64vNV(buffer, pname, params);
	}

	@Override
	public void glGetNamedBufferParameterui64vNV(final int buffer, final int pname, final long[] params,
			final int params_offset) {
		super.glGetNamedBufferParameterui64vNV(buffer, pname, params, params_offset);
	}

	@Override
	public void glGetIntegerui64vNV(final int value, final LongBuffer result) {
		super.glGetIntegerui64vNV(value, result);
	}

	@Override
	public void glGetIntegerui64vNV(final int value, final long[] result, final int result_offset) {
		super.glGetIntegerui64vNV(value, result, result_offset);
	}

	@Override
	public void glUniformui64NV(final int location, final long value) {
		super.glUniformui64NV(location, value);
	}

	@Override
	public void glUniformui64vNV(final int location, final int count, final LongBuffer value) {
		super.glUniformui64vNV(location, count, value);
	}

	@Override
	public void glUniformui64vNV(final int location, final int count, final long[] value, final int value_offset) {
		super.glUniformui64vNV(location, count, value, value_offset);
	}

	@Override
	public void glProgramUniformui64NV(final int program, final int location, final long value) {
		super.glProgramUniformui64NV(program, location, value);
	}

	@Override
	public void glProgramUniformui64vNV(final int program, final int location, final int count,
			final LongBuffer value) {
		super.glProgramUniformui64vNV(program, location, count, value);
	}

	@Override
	public void glProgramUniformui64vNV(final int program, final int location, final int count, final long[] value,
			final int value_offset) {
		super.glProgramUniformui64vNV(program, location, count, value, value_offset);
	}

	@Override
	public void glTextureBarrierNV() {
		super.glTextureBarrierNV();
	}

	@Override
	public void glTexImage2DMultisampleCoverageNV(final int target, final int coverageSamples, final int colorSamples,
			final int internalFormat, final int width, final int height, final boolean fixedSampleLocations) {
		super.glTexImage2DMultisampleCoverageNV(target, coverageSamples, colorSamples, internalFormat, width, height,
				fixedSampleLocations);
	}

	@Override
	public void glTexImage3DMultisampleCoverageNV(final int target, final int coverageSamples, final int colorSamples,
			final int internalFormat, final int width, final int height, final int depth,
			final boolean fixedSampleLocations) {
		super.glTexImage3DMultisampleCoverageNV(target, coverageSamples, colorSamples, internalFormat, width, height,
				depth, fixedSampleLocations);
	}

	@Override
	public void glTextureImage2DMultisampleNV(final int texture, final int target, final int samples,
			final int internalFormat, final int width, final int height, final boolean fixedSampleLocations) {
		super.glTextureImage2DMultisampleNV(texture, target, samples, internalFormat, width, height,
				fixedSampleLocations);
	}

	@Override
	public void glTextureImage3DMultisampleNV(final int texture, final int target, final int samples,
			final int internalFormat, final int width, final int height, final int depth,
			final boolean fixedSampleLocations) {
		super.glTextureImage3DMultisampleNV(texture, target, samples, internalFormat, width, height, depth,
				fixedSampleLocations);
	}

	@Override
	public void glTextureImage2DMultisampleCoverageNV(final int texture, final int target, final int coverageSamples,
			final int colorSamples, final int internalFormat, final int width, final int height,
			final boolean fixedSampleLocations) {
		super.glTextureImage2DMultisampleCoverageNV(texture, target, coverageSamples, colorSamples, internalFormat,
				width, height, fixedSampleLocations);
	}

	@Override
	public void glTextureImage3DMultisampleCoverageNV(final int texture, final int target, final int coverageSamples,
			final int colorSamples, final int internalFormat, final int width, final int height, final int depth,
			final boolean fixedSampleLocations) {
		super.glTextureImage3DMultisampleCoverageNV(texture, target, coverageSamples, colorSamples, internalFormat,
				width, height, depth, fixedSampleLocations);
	}

	@Override
	public void glBindTransformFeedbackNV(final int target, final int id) {
		super.glBindTransformFeedbackNV(target, id);
	}

	@Override
	public void glDeleteTransformFeedbacksNV(final int n, final IntBuffer ids) {
		super.glDeleteTransformFeedbacksNV(n, ids);
	}

	@Override
	public void glDeleteTransformFeedbacksNV(final int n, final int[] ids, final int ids_offset) {
		super.glDeleteTransformFeedbacksNV(n, ids, ids_offset);
	}

	@Override
	public void glGenTransformFeedbacksNV(final int n, final IntBuffer ids) {
		super.glGenTransformFeedbacksNV(n, ids);
	}

	@Override
	public void glGenTransformFeedbacksNV(final int n, final int[] ids, final int ids_offset) {
		super.glGenTransformFeedbacksNV(n, ids, ids_offset);
	}

	@Override
	public boolean glIsTransformFeedbackNV(final int id) {
		return super.glIsTransformFeedbackNV(id);
	}

	@Override
	public void glPauseTransformFeedbackNV() {
		super.glPauseTransformFeedbackNV();
	}

	@Override
	public void glResumeTransformFeedbackNV() {
		super.glResumeTransformFeedbackNV();
	}

	@Override
	public void glDrawTransformFeedbackNV(final int mode, final int id) {
		super.glDrawTransformFeedbackNV(mode, id);
	}

	@Override
	public void glVDPAUInitNV(final Buffer vdpDevice, final Buffer getProcAddress) {
		super.glVDPAUInitNV(vdpDevice, getProcAddress);
	}

	@Override
	public void glVDPAUFiniNV() {
		super.glVDPAUFiniNV();
	}

	@Override
	public long glVDPAURegisterVideoSurfaceNV(final Buffer vdpSurface, final int target, final int numTextureNames,
			final IntBuffer textureNames) {
		return super.glVDPAURegisterVideoSurfaceNV(vdpSurface, target, numTextureNames, textureNames);
	}

	@Override
	public long glVDPAURegisterVideoSurfaceNV(final Buffer vdpSurface, final int target, final int numTextureNames,
			final int[] textureNames, final int textureNames_offset) {
		return super.glVDPAURegisterVideoSurfaceNV(vdpSurface, target, numTextureNames, textureNames,
				textureNames_offset);
	}

	@Override
	public long glVDPAURegisterOutputSurfaceNV(final Buffer vdpSurface, final int target, final int numTextureNames,
			final IntBuffer textureNames) {
		return super.glVDPAURegisterOutputSurfaceNV(vdpSurface, target, numTextureNames, textureNames);
	}

	@Override
	public long glVDPAURegisterOutputSurfaceNV(final Buffer vdpSurface, final int target, final int numTextureNames,
			final int[] textureNames, final int textureNames_offset) {
		return super.glVDPAURegisterOutputSurfaceNV(vdpSurface, target, numTextureNames, textureNames,
				textureNames_offset);
	}

	@Override
	public boolean glVDPAUIsSurfaceNV(final long surface) {
		return super.glVDPAUIsSurfaceNV(surface);
	}

	@Override
	public void glVDPAUUnregisterSurfaceNV(final long surface) {
		super.glVDPAUUnregisterSurfaceNV(surface);
	}

	@Override
	public void glVDPAUGetSurfaceivNV(final long surface, final int pname, final int bufSize, final IntBuffer length,
			final IntBuffer values) {
		super.glVDPAUGetSurfaceivNV(surface, pname, bufSize, length, values);
	}

	@Override
	public void glVDPAUGetSurfaceivNV(final long surface, final int pname, final int bufSize, final int[] length,
			final int length_offset, final int[] values, final int values_offset) {
		super.glVDPAUGetSurfaceivNV(surface, pname, bufSize, length, length_offset, values, values_offset);
	}

	@Override
	public void glVDPAUSurfaceAccessNV(final long surface, final int access) {
		super.glVDPAUSurfaceAccessNV(surface, access);
	}

	@Override
	public void glVDPAUMapSurfacesNV(final int numSurfaces, final PointerBuffer surfaces) {
		super.glVDPAUMapSurfacesNV(numSurfaces, surfaces);
	}

	@Override
	public void glVDPAUUnmapSurfacesNV(final int numSurface, final PointerBuffer surfaces) {
		super.glVDPAUUnmapSurfacesNV(numSurface, surfaces);
	}

	@Override
	public void glVertexAttribL1i64NV(final int index, final long x) {
		super.glVertexAttribL1i64NV(index, x);
	}

	@Override
	public void glVertexAttribL2i64NV(final int index, final long x, final long y) {
		super.glVertexAttribL2i64NV(index, x, y);
	}

	@Override
	public void glVertexAttribL3i64NV(final int index, final long x, final long y, final long z) {
		super.glVertexAttribL3i64NV(index, x, y, z);
	}

	@Override
	public void glVertexAttribL4i64NV(final int index, final long x, final long y, final long z, final long w) {
		super.glVertexAttribL4i64NV(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribL1i64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL1i64vNV(index, v);
	}

	@Override
	public void glVertexAttribL1i64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL1i64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL2i64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL2i64vNV(index, v);
	}

	@Override
	public void glVertexAttribL2i64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL2i64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL3i64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL3i64vNV(index, v);
	}

	@Override
	public void glVertexAttribL3i64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL3i64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL4i64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL4i64vNV(index, v);
	}

	@Override
	public void glVertexAttribL4i64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL4i64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL1ui64NV(final int index, final long x) {
		super.glVertexAttribL1ui64NV(index, x);
	}

	@Override
	public void glVertexAttribL2ui64NV(final int index, final long x, final long y) {
		super.glVertexAttribL2ui64NV(index, x, y);
	}

	@Override
	public void glVertexAttribL3ui64NV(final int index, final long x, final long y, final long z) {
		super.glVertexAttribL3ui64NV(index, x, y, z);
	}

	@Override
	public void glVertexAttribL4ui64NV(final int index, final long x, final long y, final long z, final long w) {
		super.glVertexAttribL4ui64NV(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribL1ui64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL1ui64vNV(index, v);
	}

	@Override
	public void glVertexAttribL1ui64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL1ui64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL2ui64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL2ui64vNV(index, v);
	}

	@Override
	public void glVertexAttribL2ui64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL2ui64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL3ui64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL3ui64vNV(index, v);
	}

	@Override
	public void glVertexAttribL3ui64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL3ui64vNV(index, v, v_offset);
	}

	@Override
	public void glVertexAttribL4ui64vNV(final int index, final LongBuffer v) {
		super.glVertexAttribL4ui64vNV(index, v);
	}

	@Override
	public void glVertexAttribL4ui64vNV(final int index, final long[] v, final int v_offset) {
		super.glVertexAttribL4ui64vNV(index, v, v_offset);
	}

	@Override
	public void glGetVertexAttribLi64vNV(final int index, final int pname, final LongBuffer params) {
		super.glGetVertexAttribLi64vNV(index, pname, params);
	}

	@Override
	public void glGetVertexAttribLi64vNV(final int index, final int pname, final long[] params,
			final int params_offset) {
		super.glGetVertexAttribLi64vNV(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribLui64vNV(final int index, final int pname, final LongBuffer params) {
		super.glGetVertexAttribLui64vNV(index, pname, params);
	}

	@Override
	public void glGetVertexAttribLui64vNV(final int index, final int pname, final long[] params,
			final int params_offset) {
		super.glGetVertexAttribLui64vNV(index, pname, params, params_offset);
	}

	@Override
	public void glVertexAttribLFormatNV(final int index, final int size, final int type, final int stride) {
		super.glVertexAttribLFormatNV(index, size, type, stride);
	}

	@Override
	public void glBufferAddressRangeNV(final int pname, final int index, final long address, final long length) {
		super.glBufferAddressRangeNV(pname, index, address, length);
	}

	@Override
	public void glVertexFormatNV(final int size, final int type, final int stride) {
		super.glVertexFormatNV(size, type, stride);
	}

	@Override
	public void glNormalFormatNV(final int type, final int stride) {
		super.glNormalFormatNV(type, stride);
	}

	@Override
	public void glColorFormatNV(final int size, final int type, final int stride) {
		super.glColorFormatNV(size, type, stride);
	}

	@Override
	public void glIndexFormatNV(final int type, final int stride) {
		super.glIndexFormatNV(type, stride);
	}

	@Override
	public void glTexCoordFormatNV(final int size, final int type, final int stride) {
		super.glTexCoordFormatNV(size, type, stride);
	}

	@Override
	public void glEdgeFlagFormatNV(final int stride) {
		super.glEdgeFlagFormatNV(stride);
	}

	@Override
	public void glSecondaryColorFormatNV(final int size, final int type, final int stride) {
		super.glSecondaryColorFormatNV(size, type, stride);
	}

	@Override
	public void glFogCoordFormatNV(final int type, final int stride) {
		super.glFogCoordFormatNV(type, stride);
	}

	@Override
	public void glVertexAttribFormatNV(final int index, final int size, final int type, final boolean normalized,
			final int stride) {
		super.glVertexAttribFormatNV(index, size, type, normalized, stride);
	}

	@Override
	public void glVertexAttribIFormatNV(final int index, final int size, final int type, final int stride) {
		super.glVertexAttribIFormatNV(index, size, type, stride);
	}

	@Override
	public void glGetIntegerui64i_vNV(final int value, final int index, final LongBuffer result) {
		super.glGetIntegerui64i_vNV(value, index, result);
	}

	@Override
	public void glGetIntegerui64i_vNV(final int value, final int index, final long[] result, final int result_offset) {
		super.glGetIntegerui64i_vNV(value, index, result, result_offset);
	}

	@Override
	public void glVertexAttribI1iEXT(final int index, final int x) {
		super.glVertexAttribI1iEXT(index, x);
	}

	@Override
	public void glVertexAttribI2iEXT(final int index, final int x, final int y) {
		super.glVertexAttribI2iEXT(index, x, y);
	}

	@Override
	public void glVertexAttribI3iEXT(final int index, final int x, final int y, final int z) {
		super.glVertexAttribI3iEXT(index, x, y, z);
	}

	@Override
	public void glVertexAttribI4iEXT(final int index, final int x, final int y, final int z, final int w) {
		super.glVertexAttribI4iEXT(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribI1uiEXT(final int index, final int x) {
		super.glVertexAttribI1uiEXT(index, x);
	}

	@Override
	public void glVertexAttribI2uiEXT(final int index, final int x, final int y) {
		super.glVertexAttribI2uiEXT(index, x, y);
	}

	@Override
	public void glVertexAttribI3uiEXT(final int index, final int x, final int y, final int z) {
		super.glVertexAttribI3uiEXT(index, x, y, z);
	}

	@Override
	public void glVertexAttribI4uiEXT(final int index, final int x, final int y, final int z, final int w) {
		super.glVertexAttribI4uiEXT(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribI1ivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI1ivEXT(index, v);
	}

	@Override
	public void glVertexAttribI1ivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI1ivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI2ivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI2ivEXT(index, v);
	}

	@Override
	public void glVertexAttribI2ivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI2ivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI3ivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI3ivEXT(index, v);
	}

	@Override
	public void glVertexAttribI3ivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI3ivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4ivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI4ivEXT(index, v);
	}

	@Override
	public void glVertexAttribI4ivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI4ivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI1uivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI1uivEXT(index, v);
	}

	@Override
	public void glVertexAttribI1uivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI1uivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI2uivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI2uivEXT(index, v);
	}

	@Override
	public void glVertexAttribI2uivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI2uivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI3uivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI3uivEXT(index, v);
	}

	@Override
	public void glVertexAttribI3uivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI3uivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4uivEXT(final int index, final IntBuffer v) {
		super.glVertexAttribI4uivEXT(index, v);
	}

	@Override
	public void glVertexAttribI4uivEXT(final int index, final int[] v, final int v_offset) {
		super.glVertexAttribI4uivEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4bvEXT(final int index, final ByteBuffer v) {
		super.glVertexAttribI4bvEXT(index, v);
	}

	@Override
	public void glVertexAttribI4bvEXT(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttribI4bvEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4svEXT(final int index, final ShortBuffer v) {
		super.glVertexAttribI4svEXT(index, v);
	}

	@Override
	public void glVertexAttribI4svEXT(final int index, final short[] v, final int v_offset) {
		super.glVertexAttribI4svEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4ubvEXT(final int index, final ByteBuffer v) {
		super.glVertexAttribI4ubvEXT(index, v);
	}

	@Override
	public void glVertexAttribI4ubvEXT(final int index, final byte[] v, final int v_offset) {
		super.glVertexAttribI4ubvEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribI4usvEXT(final int index, final ShortBuffer v) {
		super.glVertexAttribI4usvEXT(index, v);
	}

	@Override
	public void glVertexAttribI4usvEXT(final int index, final short[] v, final int v_offset) {
		super.glVertexAttribI4usvEXT(index, v, v_offset);
	}

	@Override
	public void glVertexAttribIPointerEXT(final int index, final int size, final int type, final int stride,
			final Buffer pointer) {
		super.glVertexAttribIPointerEXT(index, size, type, stride, pointer);
	}

	@Override
	public void glGetVertexAttribIivEXT(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribIivEXT(index, pname, params);
	}

	@Override
	public void glGetVertexAttribIivEXT(final int index, final int pname, final int[] params, final int params_offset) {
		super.glGetVertexAttribIivEXT(index, pname, params, params_offset);
	}

	@Override
	public void glGetVertexAttribIuivEXT(final int index, final int pname, final IntBuffer params) {
		super.glGetVertexAttribIuivEXT(index, pname, params);
	}

	@Override
	public void glGetVertexAttribIuivEXT(final int index, final int pname, final int[] params,
			final int params_offset) {
		super.glGetVertexAttribIuivEXT(index, pname, params, params_offset);
	}

	@Override
	public void glBeginVideoCaptureNV(final int video_capture_slot) {
		super.glBeginVideoCaptureNV(video_capture_slot);
	}

	@Override
	public void glBindVideoCaptureStreamBufferNV(final int video_capture_slot, final int stream, final int frame_region,
			final long offset) {
		super.glBindVideoCaptureStreamBufferNV(video_capture_slot, stream, frame_region, offset);
	}

	@Override
	public void glBindVideoCaptureStreamTextureNV(final int video_capture_slot, final int stream,
			final int frame_region, final int target, final int texture) {
		super.glBindVideoCaptureStreamTextureNV(video_capture_slot, stream, frame_region, target, texture);
	}

	@Override
	public void glEndVideoCaptureNV(final int video_capture_slot) {
		super.glEndVideoCaptureNV(video_capture_slot);
	}

	@Override
	public void glGetVideoCaptureivNV(final int video_capture_slot, final int pname, final IntBuffer params) {
		super.glGetVideoCaptureivNV(video_capture_slot, pname, params);
	}

	@Override
	public void glGetVideoCaptureivNV(final int video_capture_slot, final int pname, final int[] params,
			final int params_offset) {
		super.glGetVideoCaptureivNV(video_capture_slot, pname, params, params_offset);
	}

	@Override
	public void glGetVideoCaptureStreamivNV(final int video_capture_slot, final int stream, final int pname,
			final IntBuffer params) {
		super.glGetVideoCaptureStreamivNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glGetVideoCaptureStreamivNV(final int video_capture_slot, final int stream, final int pname,
			final int[] params, final int params_offset) {
		super.glGetVideoCaptureStreamivNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public void glGetVideoCaptureStreamfvNV(final int video_capture_slot, final int stream, final int pname,
			final FloatBuffer params) {
		super.glGetVideoCaptureStreamfvNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glGetVideoCaptureStreamfvNV(final int video_capture_slot, final int stream, final int pname,
			final float[] params, final int params_offset) {
		super.glGetVideoCaptureStreamfvNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public void glGetVideoCaptureStreamdvNV(final int video_capture_slot, final int stream, final int pname,
			final DoubleBuffer params) {
		super.glGetVideoCaptureStreamdvNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glGetVideoCaptureStreamdvNV(final int video_capture_slot, final int stream, final int pname,
			final double[] params, final int params_offset) {
		super.glGetVideoCaptureStreamdvNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public int glVideoCaptureNV(final int video_capture_slot, final IntBuffer sequence_num,
			final LongBuffer capture_time) {
		return super.glVideoCaptureNV(video_capture_slot, sequence_num, capture_time);
	}

	@Override
	public int glVideoCaptureNV(final int video_capture_slot, final int[] sequence_num, final int sequence_num_offset,
			final long[] capture_time, final int capture_time_offset) {
		return super.glVideoCaptureNV(video_capture_slot, sequence_num, sequence_num_offset, capture_time,
				capture_time_offset);
	}

	@Override
	public void glVideoCaptureStreamParameterivNV(final int video_capture_slot, final int stream, final int pname,
			final IntBuffer params) {
		super.glVideoCaptureStreamParameterivNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glVideoCaptureStreamParameterivNV(final int video_capture_slot, final int stream, final int pname,
			final int[] params, final int params_offset) {
		super.glVideoCaptureStreamParameterivNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public void glVideoCaptureStreamParameterfvNV(final int video_capture_slot, final int stream, final int pname,
			final FloatBuffer params) {
		super.glVideoCaptureStreamParameterfvNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glVideoCaptureStreamParameterfvNV(final int video_capture_slot, final int stream, final int pname,
			final float[] params, final int params_offset) {
		super.glVideoCaptureStreamParameterfvNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public void glVideoCaptureStreamParameterdvNV(final int video_capture_slot, final int stream, final int pname,
			final DoubleBuffer params) {
		super.glVideoCaptureStreamParameterdvNV(video_capture_slot, stream, pname, params);
	}

	@Override
	public void glVideoCaptureStreamParameterdvNV(final int video_capture_slot, final int stream, final int pname,
			final double[] params, final int params_offset) {
		super.glVideoCaptureStreamParameterdvNV(video_capture_slot, stream, pname, params, params_offset);
	}

	@Override
	public void glFramebufferTextureMultiviewOVR(final int target, final int attachment, final int texture,
			final int level, final int baseViewIndex, final int numViews) {
		super.glFramebufferTextureMultiviewOVR(target, attachment, texture, level, baseViewIndex, numViews);
	}

	@Override
	public void glHintPGI(final int target, final int mode) {
		super.glHintPGI(target, mode);
	}

	@Override
	public void glFinishTextureSUNX() {
		super.glFinishTextureSUNX();
	}

	@Override
	public GLProfile getGLProfile() {
		return super.getGLProfile();
	}

	@Override
	public boolean glUnmapBuffer(final int target) {
		return super.glUnmapBuffer(target);
	}

	@Override
	public boolean glUnmapNamedBuffer(final int buffer) {
		return super.glUnmapNamedBuffer(buffer);
	}

}
