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

    // State untuk form tambah
    var inputTitle by rememberSaveable {
        mutableStateOf("")
    }

    var inputNote by rememberSaveable {
        mutableStateOf("")
    }

    // State untuk form edit
    var editingId by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    var editingTitle by rememberSaveable {
        mutableStateOf("")
    }

    var editingNote by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Judul aplikasi
        Text(
            text = "Catatan Saya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Form tambah Title
        OutlinedTextField(
            value = inputTitle,
            onValueChange = {
                inputTitle = it
            },
            label = {
                Text("Title")
            },
            placeholder = {
                Text("Masukkan judul catatan")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Form tambah Note
        OutlinedTextField(
            value = inputNote,
            onValueChange = {
                inputNote = it
            },
            label = {
                Text("Note")
            },
            placeholder = {
                Text("Masukkan isi catatan")
            },
            minLines = 3,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol Tambah
        Button(
            onClick = {
                vm.addNote(inputTitle, inputNote)

                inputTitle = ""
                inputNote = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text("Tambah Catatan")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Jumlah catatan
        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Daftar catatan
        if (notes.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada catatan",
                    color = Color.Gray
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                items(
                    items = notes,
                    key = { it.id }
                ) { note ->

                    if (editingId == note.id) {

                        // Tampilan ketika sedang mengedit
                        NoteItemEditing(
                            title = editingTitle,
                            note = editingNote,

                            onTitleChange = {
                                editingTitle = it
                            },

                            onNoteChange = {
                                editingNote = it
                            },

                            onSave = {
                                if (
                                    editingTitle.isNotBlank() &&
                                    editingNote.isNotBlank()
                                ) {
                                    vm.updateNote(
                                        note,
                                        editingTitle,
                                        editingNote
                                    )

                                    editingId = null
                                    editingTitle = ""
                                    editingNote = ""
                                }
                            },

                            onCancel = {
                                editingId = null
                                editingTitle = ""
                                editingNote = ""
                            }
                        )

                    } else {

                        // Tampilan catatan normal
                        NoteItem(
                            note = note,

                            onEdit = {
                                editingId = note.id
                                editingTitle = note.title
                                editingNote = note.note
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

// Komponen untuk menampilkan catatan
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    ListItem(
        headlineContent = {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold
            )
        },

        supportingContent = {
            Column {
                Text(
                    text = note.note
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: ${note.id}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },

        trailingContent = {
            Row {

                // Tombol Edit
                IconButton(
                    onClick = onEdit
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = "Edit",
                        tint = Color(0xFF2196F3)
                    )
                }

                // Tombol Hapus
                IconButton(
                    onClick = onDelete
                ) {
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

// Komponen untuk form edit catatan
@Composable
fun NoteItemEditing(
    title: String,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        // Input edit Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = {
                Text("Title")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input edit Note
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = {
                Text("Note")
            },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tombol Simpan dan Batal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            // Tombol Batal
            IconButton(
                onClick = onCancel
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Batal"
                )
            }

            // Tombol Simpan
            IconButton(
                onClick = onSave
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Simpan",
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}

// Preview NoteItem
@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {

    NotesAppTheme {
        NoteItem(
            note = Note(
                id = 1,
                title = "Belajar Kotlin",
                note = "Mempelajari dasar bahasa pemrograman Kotlin"
            )
        )
    }
}
