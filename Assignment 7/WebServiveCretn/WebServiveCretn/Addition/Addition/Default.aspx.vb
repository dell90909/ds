Partial Public Class _Default
    Inherits System.Web.UI.Page

    Protected Sub Page_Load(ByVal sender As Object, ByVal e As System.EventArgs) Handles Me.Load

    End Sub

    Protected Sub Button1_Click(ByVal sender As Object, ByVal e As EventArgs) Handles Button1.Click
        Dim o As New service.Service1
        Label3.Text = o.Addition(TextBox1.Text, TextBox2.Text)
    End Sub

    Protected Sub Button2_Click(ByVal sender As Object, ByVal e As EventArgs) Handles Button2.Click
        Dim o As New service.Service1
        Label4.Text = o.leap(TextBox2.Text)
        If Label4.Text = 1 Then
            MsgBox("Year is leap year")
        Else
            MsgBox("Year is not  year")
        End If
    End Sub
End Class