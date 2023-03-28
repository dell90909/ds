Imports System.Web.Services
Imports System.Web.Services.Protocols
Imports System.ComponentModel

' To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line.
' <System.Web.Script.Services.ScriptService()> _
<System.Web.Services.WebService(Namespace:="http://tempuri.org/")> _
<System.Web.Services.WebServiceBinding(ConformsTo:=WsiProfiles.BasicProfile1_1)> _
<ToolboxItem(False)> _
Public Class Service1
    Inherits System.Web.Services.WebService

    <WebMethod()> _
    Public Function HelloWorld() As String
       Return "Hello World"
    End Function

    <WebMethod()> _
    Public Function Addition(ByVal a As String, ByVal b As String) As String
        Return (Val(a) + Val(b))
    End Function

    <WebMethod()> _
  Public Function leap(ByVal a As String) As String

        If a Mod 4 = 0 Then
            Return 1
        Else
            Return 0
        End If


    End Function

End Class