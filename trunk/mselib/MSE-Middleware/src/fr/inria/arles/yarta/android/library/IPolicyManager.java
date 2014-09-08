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
case TRANSACTION_loadPolicies:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.loadPolicies(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
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
@Override public boolean loadPolicies(java.lang.String path) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
mRemote.transact(Stub.TRANSACTION_loadPolicies, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_loadPolicies = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public boolean loadPolicies(java.lang.String path) throws android.os.RemoteException;
}
