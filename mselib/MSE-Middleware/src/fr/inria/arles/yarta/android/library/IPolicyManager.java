/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/grosca/work/yarta/trunk/mselib/libapps/android/YartaLibrary/src/fr/inria/arles/yarta/android/library/IPolicyManager.aidl
 */
package fr.inria.arles.yarta.android.library;
public interface IPolicyManager extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements fr.inria.arles.yarta.android.library.IPolicyManager
{
private static final java.lang.String DESCRIPTOR = "fr.inria.arles.yarta.android.library.IPolicyManager";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an fr.inria.arles.yarta.android.library.IPolicyManager interface,
 * generating a proxy if needed.
 */
public static fr.inria.arles.yarta.android.library.IPolicyManager asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof fr.inria.arles.yarta.android.library.IPolicyManager))) {
return ((fr.inria.arles.yarta.android.library.IPolicyManager)iin);
}
return new fr.inria.arles.yarta.android.library.IPolicyManager.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getRulesCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getRulesCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getRule:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getRule(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_removeRule:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.removeRule(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addRule:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.addRule(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements fr.inria.arles.yarta.android.library.IPolicyManager
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getRulesCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getRulesCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getRule(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_getRule, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void removeRule(int position) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
mRemote.transact(Stub.TRANSACTION_removeRule, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void addRule(java.lang.String rule) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(rule);
mRemote.transact(Stub.TRANSACTION_addRule, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getRulesCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_removeRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_addRule = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public int getRulesCount() throws android.os.RemoteException;
public java.lang.String getRule(int position) throws android.os.RemoteException;
public void removeRule(int position) throws android.os.RemoteException;
public void addRule(java.lang.String rule) throws android.os.RemoteException;
}
