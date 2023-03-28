Public Class Form1
    Dim t1, t2, t3 As Integer
    

    Private Sub Form1_Load(ByVal sender As System.Object, ByVal e As System.EventArgs)
        t1 = 6
        t2 = 8
        t3 = 10
    End Sub

  

    Private Sub Button5_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button5.Click
        Timer2.Enabled = False
        If t2 > t1 Then

            MsgBox(" Msg is from P1--->P2 and P2 took" & t2 - t1 + 8 & "  ticks to recive ")
        Else
            t2 = t1 + 1
            ListBox5.Items.Add(t2)
            MsgBox("Updated Process P2 ")
        End If
        Timer2.Enabled = True
    End Sub

    Private Sub Button6_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button6.Click
        Timer2.Enabled = False
        If t3 > t2 Then
            MsgBox("Msg is from P2--->P3 and P3 took" & t3 - t2 + 10 & "  ticks to recive ")
        Else
            t3 = t2 + 1
            ListBox6.Items.Add(t3)
            MsgBox("Updated Process P3")
        End If
        Timer2.Enabled = True
    End Sub

    Private Sub Button7_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button7.Click
        Timer2.Enabled = False
        If t1 > t2 Then
            MsgBox("Msg is from P2--->P1 and P1 took" & t1 - t2 + 6 & "  ticks to recive ")
        Else
            t1 = t2 + 1
            ListBox4.Items.Add(t1)
            MsgBox("Updated Process P1")
        End If
        Timer2.Enabled = True
    End Sub

    Private Sub Button8_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button8.Click
        Timer2.Enabled = False
        If t2 > t3 Then
            MsgBox("Msg is from P3--->P2 and P2 took" & t2 - t3 + 8 & "  ticks to recive ")
        Else
            t2 = t3 + 1
            ListBox5.Items.Add(t2)
            MsgBox("Updated Process P2")
        End If
        Timer2.Enabled = True
    End Sub

    Private Sub Timer2_Tick(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Timer2.Tick
        t1 = t1 + 6
        t2 = t2 + 8
        t3 = t3 + 10
        ListBox4.Items.Add(t1)
        ListBox5.Items.Add(t2)
        ListBox6.Items.Add(t3)
    End Sub

    Private Sub Form1_Load_1(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles MyBase.Load

    End Sub

    Private Sub Button9_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button9.Click
        End
    End Sub
End Class
