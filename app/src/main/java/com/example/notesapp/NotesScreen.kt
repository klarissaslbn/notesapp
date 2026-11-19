package com.example.notesapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.ui.theme.NotesAppTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
) {
    val notes by vm.notes.collectAsState()

    var input by rememberSaveable {
        mutableStateOf("")
    }

    var editingId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editingText by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Catatan Saya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // Form input catatan baru
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = {
                    Text("Catatan baru")
                },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    vm.addNote(input)
                    input = ""
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )

                Spacer(
                    Modifier.width(ButtonDefaults.IconSpacing)
                )

                Text("Tambah")
            }
        }

        // Jumlah catatan
        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Daftar catatan
        if (notes.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada catatan",
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    notes,
                    key = { it.id }
                ) { note ->

                    if (editingId == note.id) {

                        // Tampilan ketika catatan sedang diedit
                        NoteItemEditing(
                            text = editingText,

                            onTextChange = {
                                editingText = it
                            },

                            onSave = {
                                vm.updateNote(note, editingText)
                                editingId = null
                            },

                            onCancel = {
                                editingId = null
                            }
                        )

                    } else {

                        // Tampilan catatan normal
                        NoteItem(
                            note = note,

                            onEdit = {
                                editingId = note.id
                                editingText = note.title
                            },

                            onDelete = {
                                vm.deleteNote(note)
                            }
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}

// Komponen untuk menampilkan satu catatan
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ListItem(
        headlineContent = {
            Text(note.title)
        },

        supportingContent = {
            Text("ID: ${note.id}")
        },

        trailingContent = {
            Row {

                // Tombol Edit
                IconButton(onClick = onEdit) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF2196F3)
                    )
                }

                // Tombol Hapus
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Hapus",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    )
}

// Preview untuk tampilan catatan
@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NotesAppTheme {
        NoteItem(
            note = Note(
                id = 1,
                title = "Belajar Room Database"
            )
        )
    }
}

// Komponen untuk mengedit catatan
@Composable
fun NoteItemEditing(
    text: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = {
                Text("Ubah catatan")
            },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        // Tombol Simpan
        IconButton(onClick = onSave) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Simpan",
                tint = Color(0xFF4CAF50)
            )
        }

        // Tombol Batal
        IconButton(onClick = onCancel) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Batal"
            )
        }
    }
}
