/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\work\\yarta\\mselib\\libapps\\android\\YartaLibrary\\src\\fr\\inria\\arles\\yarta\\android\\library\\ILibraryService.aidl
 */
package fr.inria.arles.yarta.android.library;
public interface ILibraryService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements fr.inria.arles.yarta.android.library.ILibraryService
{
private static final java.lang.String DESCRIPTOR = "fr.inria.arles.yarta.android.library.ILibraryService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an fr.inria.arles.yarta.android.library.ILibraryService interface,
 * generating a proxy if needed.
 */
public static fr.inria.arles.yarta.android.library.ILibraryService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof fr.inria.arles.yarta.android.library.ILibraryService))) {
return ((fr.inria.arles.yarta.android.library.ILibraryService)iin);
}
return new fr.inria.arles.yarta.android.library.ILibraryService.Stub.Proxy(obj);
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
case TRANSACTION_getUserId:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getUserId();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_clear:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.clear();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_initialize:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IMSEApplication _arg0;
_arg0 = fr.inria.arles.yarta.android.library.IMSEApplication.Stub.asInterface(data.readStrongBinder());
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
this.initialize(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
return true;
}
case TRANSACTION_uninitialize:
{
data.enforceInterface(DESCRIPTOR);
this.uninitialize();
reply.writeNoException();
return true;
}
case TRANSACTION_addLiteral:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
android.os.Bundle _result = this.addLiteral(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addResource:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
android.os.Bundle _result = this.addResource(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addResourceNode:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
android.os.Bundle _result = this.addResourceNode(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getMyNameSpace:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getMyNameSpace();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_addTriple:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
android.os.Bundle _arg2;
if ((0!=data.readInt())) {
_arg2 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
java.lang.String _arg3;
_arg3 = data.readString();
android.os.Bundle _result = this.addTriple(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getTriple:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
android.os.Bundle _arg2;
if ((0!=data.readInt())) {
_arg2 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
java.lang.String _arg3;
_arg3 = data.readString();
android.os.Bundle _result = this.getTriple(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_removeTriple:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
android.os.Bundle _arg2;
if ((0!=data.readInt())) {
_arg2 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
java.lang.String _arg3;
_arg3 = data.readString();
android.os.Bundle _result = this.removeTriple(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getResourceByURI:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
android.os.Bundle _result = this.getResourceByURI(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getResourceByURINoPolicies:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.Bundle _result = this.getResourceByURINoPolicies(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPropertyObjectAsTriples:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
java.util.List<android.os.Bundle> _result = this.getPropertyObjectAsTriples(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getPropertySubjectAsTriples:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
java.util.List<android.os.Bundle> _result = this.getPropertySubjectAsTriples(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getPropertyAsTriples:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
java.util.List<android.os.Bundle> _result = this.getPropertyAsTriples(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getAllPropertiesAsTriples:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
java.util.List<android.os.Bundle> _result = this.getAllPropertiesAsTriples(_arg0, _arg1);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getKBAsTriples:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.util.List<android.os.Bundle> _result = this.getKBAsTriples(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getPropertyObject:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
android.os.Bundle _result = this.getPropertyObject(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPropertySubject:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
android.os.Bundle _result = this.getPropertySubject(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addGraph:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
android.os.Bundle _result = this.addGraph(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getProperty:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
java.lang.String _arg2;
_arg2 = data.readString();
android.os.Bundle _result = this.getProperty(_arg0, _arg1, _arg2);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getAllProperties:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
android.os.Bundle _result = this.getAllProperties(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_queryKB:
{
data.enforceInterface(DESCRIPTOR);
android.os.Bundle _arg0;
if ((0!=data.readInt())) {
_arg0 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _arg1;
_arg1 = data.readString();
android.os.Bundle _result = this.queryKB(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getKB:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.Bundle _result = this.getKB(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPolicyManager:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IPolicyManager _result = this.getPolicyManager();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_sendUpdateRequest:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.sendUpdateRequest(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendHello:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.sendHello(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendNotify:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.sendNotify(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.os.Bundle _arg1;
if ((0!=data.readInt())) {
_arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.sendMessage(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerReceiver:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IReceiver _arg0;
_arg0 = fr.inria.arles.yarta.android.library.IReceiver.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerReceiver(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterReceiver:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IReceiver _arg0;
_arg0 = fr.inria.arles.yarta.android.library.IReceiver.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterReceiver(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IMSEApplication _arg0;
_arg0 = fr.inria.arles.yarta.android.library.IMSEApplication.Stub.asInterface(data.readStrongBinder());
boolean _result = this.registerCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
fr.inria.arles.yarta.android.library.IMSEApplication _arg0;
_arg0 = fr.inria.arles.yarta.android.library.IMSEApplication.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterCallback(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements fr.inria.arles.yarta.android.library.ILibraryService
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
@Override public java.lang.String getUserId() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getUserId, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean clear() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_clear, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*** KnowledgeBase related functions */
@Override public void initialize(fr.inria.arles.yarta.android.library.IMSEApplication app, java.lang.String source, java.lang.String namespace, java.lang.String policyFile, java.lang.String userId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((app!=null))?(app.asBinder()):(null)));
_data.writeString(source);
_data.writeString(namespace);
_data.writeString(policyFile);
_data.writeString(userId);
mRemote.transact(Stub.TRANSACTION_initialize, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void uninitialize() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_uninitialize, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public android.os.Bundle addLiteral(java.lang.String value, java.lang.String dataType, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
_data.writeString(dataType);
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_addLiteral, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle addResource(java.lang.String nodeURI, java.lang.String typeURI, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(nodeURI);
_data.writeString(typeURI);
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_addResource, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle addResourceNode(android.os.Bundle node, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((node!=null)) {
_data.writeInt(1);
node.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_addResourceNode, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String getMyNameSpace() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMyNameSpace, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle addTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_addTriple, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getTriple, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle removeTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_removeTriple, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getResourceByURI(java.lang.String URI, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(URI);
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getResourceByURI, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getResourceByURINoPolicies(java.lang.String URI) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(URI);
mRemote.transact(Stub.TRANSACTION_getResourceByURINoPolicies, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getPropertyObjectAsTriples(android.os.Bundle s, android.os.Bundle p, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getPropertyObjectAsTriples, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getPropertySubjectAsTriples(android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getPropertySubjectAsTriples, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getPropertyAsTriples(android.os.Bundle s, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getPropertyAsTriples, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getAllPropertiesAsTriples(android.os.Bundle s, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getAllPropertiesAsTriples, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getKBAsTriples(java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getKBAsTriples, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getPropertyObject(android.os.Bundle s, android.os.Bundle p, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getPropertyObject, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getPropertySubject(android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getPropertySubject, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle addGraph(android.os.Bundle g, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((g!=null)) {
_data.writeInt(1);
g.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_addGraph, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getProperty(android.os.Bundle s, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((o!=null)) {
_data.writeInt(1);
o.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getProperty, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getAllProperties(android.os.Bundle s, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((s!=null)) {
_data.writeInt(1);
s.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getAllProperties, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle queryKB(android.os.Bundle c, java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((c!=null)) {
_data.writeInt(1);
c.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_queryKB, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.os.Bundle getKB(java.lang.String requestorId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.Bundle _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(requestorId);
mRemote.transact(Stub.TRANSACTION_getKB, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.os.Bundle.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public fr.inria.arles.yarta.android.library.IPolicyManager getPolicyManager() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
fr.inria.arles.yarta.android.library.IPolicyManager _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPolicyManager, _data, _reply, 0);
_reply.readException();
_result = fr.inria.arles.yarta.android.library.IPolicyManager.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*** CommunicationManager related functions */
@Override public int sendUpdateRequest(java.lang.String peerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(peerId);
mRemote.transact(Stub.TRANSACTION_sendUpdateRequest, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendHello(java.lang.String peerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(peerId);
mRemote.transact(Stub.TRANSACTION_sendHello, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendNotify(java.lang.String peerId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(peerId);
mRemote.transact(Stub.TRANSACTION_sendNotify, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int sendMessage(java.lang.String peerId, android.os.Bundle message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(peerId);
if ((message!=null)) {
_data.writeInt(1);
message.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean registerReceiver(fr.inria.arles.yarta.android.library.IReceiver receiver) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((receiver!=null))?(receiver.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerReceiver, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean unregisterReceiver(fr.inria.arles.yarta.android.library.IReceiver receiver) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((receiver!=null))?(receiver.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterReceiver, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*** MSEApplication binding related functions */
@Override public boolean registerCallback(fr.inria.arles.yarta.android.library.IMSEApplication callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean unregisterCallback(fr.inria.arles.yarta.android.library.IMSEApplication callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
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
static final int TRANSACTION_getUserId = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_clear = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_initialize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_uninitialize = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_addLiteral = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_addResource = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_addResourceNode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getMyNameSpace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_addTriple = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_getTriple = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_removeTriple = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_getResourceByURI = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getResourceByURINoPolicies = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getPropertyObjectAsTriples = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getPropertySubjectAsTriples = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_getPropertyAsTriples = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getAllPropertiesAsTriples = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_getKBAsTriples = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getPropertyObject = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getPropertySubject = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_addGraph = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_getProperty = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_getAllProperties = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_queryKB = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getKB = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_getPolicyManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_sendUpdateRequest = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_sendHello = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_sendNotify = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_registerReceiver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_unregisterReceiver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
}
public java.lang.String getUserId() throws android.os.RemoteException;
public boolean clear() throws android.os.RemoteException;
/*** KnowledgeBase related functions */
public void initialize(fr.inria.arles.yarta.android.library.IMSEApplication app, java.lang.String source, java.lang.String namespace, java.lang.String policyFile, java.lang.String userId) throws android.os.RemoteException;
public void uninitialize() throws android.os.RemoteException;
public android.os.Bundle addLiteral(java.lang.String value, java.lang.String dataType, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle addResource(java.lang.String nodeURI, java.lang.String typeURI, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle addResourceNode(android.os.Bundle node, java.lang.String requestorId) throws android.os.RemoteException;
public java.lang.String getMyNameSpace() throws android.os.RemoteException;
public android.os.Bundle addTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle removeTriple(android.os.Bundle s, android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getResourceByURI(java.lang.String URI, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getResourceByURINoPolicies(java.lang.String URI) throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getPropertyObjectAsTriples(android.os.Bundle s, android.os.Bundle p, java.lang.String requestorId) throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getPropertySubjectAsTriples(android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getPropertyAsTriples(android.os.Bundle s, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getAllPropertiesAsTriples(android.os.Bundle s, java.lang.String requestorId) throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getKBAsTriples(java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getPropertyObject(android.os.Bundle s, android.os.Bundle p, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getPropertySubject(android.os.Bundle p, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle addGraph(android.os.Bundle g, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getProperty(android.os.Bundle s, android.os.Bundle o, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getAllProperties(android.os.Bundle s, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle queryKB(android.os.Bundle c, java.lang.String requestorId) throws android.os.RemoteException;
public android.os.Bundle getKB(java.lang.String requestorId) throws android.os.RemoteException;
public fr.inria.arles.yarta.android.library.IPolicyManager getPolicyManager() throws android.os.RemoteException;
/*** CommunicationManager related functions */
public int sendUpdateRequest(java.lang.String peerId) throws android.os.RemoteException;
public int sendHello(java.lang.String peerId) throws android.os.RemoteException;
public int sendNotify(java.lang.String peerId) throws android.os.RemoteException;
public int sendMessage(java.lang.String peerId, android.os.Bundle message) throws android.os.RemoteException;
public boolean registerReceiver(fr.inria.arles.yarta.android.library.IReceiver receiver) throws android.os.RemoteException;
public boolean unregisterReceiver(fr.inria.arles.yarta.android.library.IReceiver receiver) throws android.os.RemoteException;
/*** MSEApplication binding related functions */
public boolean registerCallback(fr.inria.arles.yarta.android.library.IMSEApplication callback) throws android.os.RemoteException;
public boolean unregisterCallback(fr.inria.arles.yarta.android.library.IMSEApplication callback) throws android.os.RemoteException;
}
