/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/grosca/work/yarta/trunk/mselib/libapps/android/YartaLibrary/src/fr/inria/arles/yarta/android/library/IMSEApplication.aidl
 */
package fr.inria.arles.yarta.android.library;
public interface IMSEApplication extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements fr.inria.arles.yarta.android.library.IMSEApplication
{
private static final java.lang.String DESCRIPTOR = "fr.inria.arles.yarta.android.library.IMSEApplication";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an fr.inria.arles.yarta.android.library.IMSEApplication interface,
 * generating a proxy if needed.
 */
public static fr.inria.arles.yarta.android.library.IMSEApplication asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof fr.inria.arles.yarta.android.library.IMSEApplication))) {
return ((fr.inria.arles.yarta.android.library.IMSEApplication)iin);
}
return new fr.inria.arles.yarta.android.library.IMSEApplication.Stub.Proxy(obj);
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
case TRANSACTION_handleNotification:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.handleNotification(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_handleQuery:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.handleQuery(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_handleKBReady:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.handleKBReady(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getAppId:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAppId();
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements fr.inria.arles.yarta.android.library.IMSEApplication
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
@Override public void handleNotification(java.lang.String notification) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(notification);
mRemote.transact(Stub.TRANSACTION_handleNotification, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean handleQuery(java.lang.String query) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(query);
mRemote.transact(Stub.TRANSACTION_handleQuery, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void handleKBReady(java.lang.String userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(userId);
mRemote.transact(Stub.TRANSACTION_handleKBReady, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getAppId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAppId, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_handleNotification = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_handleQuery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_handleKBReady = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getAppId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void handleNotification(java.lang.String notification) throws android.os.RemoteException;
public boolean handleQuery(java.lang.String query) throws android.os.RemoteException;
public void handleKBReady(java.lang.String userId) throws android.os.RemoteException;
public java.lang.String getAppId() throws android.os.RemoteException;
}
