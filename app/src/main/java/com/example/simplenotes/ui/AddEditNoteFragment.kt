package com.example.simplenotes.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simplenotes.R
import com.example.simplenotes.data.entity.Note
import com.example.simplenotes.databinding.FragmentAddeditnotesBinding
import com.example.simplenotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_addeditnotes) {

    private val viewModel by viewModels<NoteViewModel>()
    private var importedTitle: String? = null
    private var importedContent: String? = null
    private var importedCategory: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddeditnotesBinding.bind(requireView())
        val args: AddEditNoteFragmentArgs by navArgs()
        val note = args.note
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        if (note != null) {
            binding.apply {
                dateCreated.text = formatter.format(note.date_created)
                titleEdit.setText(note.title)
                categoryEdit.setText(note.category)
                contentEdit.setText(note.content)
                saveBtn.setOnClickListener {
                    val title = titleEdit.text.toString()
                    val content = contentEdit.text.toString()
                    val category = categoryEdit.text.toString()

                    val updatedNote = note.copy(
                        title = title,
                        content = content,
                        category = category,
                        date = System.currentTimeMillis()
                    )
                    viewModel.updateNote(updatedNote)
                    navigateToNoteFragment()
                }
            }
        } else {
            binding.apply {
                dateCreated.text = formatter.format(System.currentTimeMillis())
                saveBtn.setOnClickListener {
                    val title = titleEdit.text.toString()
                    val content = contentEdit.text.toString()
                    val category = categoryEdit.text.toString()
                    val note = Note(
                        title = title,
                        content = content,
                        date_created = System.currentTimeMillis(),
                        category = category,
                        date = System.currentTimeMillis()
                    )
                    viewModel.insertNote(note)
                    navigateToNoteFragment()
                }
            }
        }

        binding.importFileBtn.setOnClickListener {
            openFilePicker()
        }
        binding.exportFileBtn.setOnClickListener {
            if (note != null){
                exportSelectedNoteToExcel(note!!)
            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesEvent.collect { event ->
                if (event is NoteViewModel.NotesEvent.NavigateToNoteFragment) {
                    Log.d("Navigation", "Navigating to NoteFragment")
                    navigateToNoteFragment()
                } else {
                    Log.d("Navigation", "No action to navigate")
                }
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importNotesFromExcel(uri)
            }
        }
    }

    private fun importNotesFromExcel(uri: Uri) {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        inputStream?.let {
            try {
                val isXlsx = uri.toString().endsWith(".xlsx")
                val workbook = if (isXlsx) {
                    XSSFWorkbook(it)
                } else {
                    HSSFWorkbook(it)
                }

                val sheet = workbook.getSheetAt(0)
                val row = sheet.getRow(0)
                importedTitle = getCellValue(row.getCell(0))
                importedContent = getCellValue(row.getCell(1))
                importedCategory = getCellValue(row.getCell(2))
                val binding = FragmentAddeditnotesBinding.bind(requireView())

                binding.titleEdit.setText(importedTitle)
                binding.contentEdit.setText(importedContent)
                binding.categoryEdit.setText(importedCategory)

                workbook.close()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                it.close()
            }
        }
    }
    private fun exportSelectedNoteToExcel(note: Note) {
        viewLifecycleOwner.lifecycleScope.launch {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Note")
            val titleRow = sheet.createRow(0)
            titleRow.createCell(0).setCellValue(note.title)
            val categoryRow = sheet.createRow(1)
            categoryRow.createCell(0).setCellValue(note.category)
            val dateCreatedRow = sheet.createRow(2)
            dateCreatedRow.createCell(0).setCellValue("Date Created: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(note.date_created)}")
            val dateRow = sheet.createRow(3)
            dateRow.createCell(0).setCellValue("Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(note.date)}")
            val contentLines = note.content.toString().split("\n")
            contentLines.forEachIndexed { index, line ->
                val contentRow = sheet.createRow(index + 4)
                contentRow.createCell(0).setCellValue(line)
            }

            val fileName = "${note.title}_${System.currentTimeMillis()}.xlsx"
            val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(documentsDir, fileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    workbook.write(outputStream)
                }
                Toast.makeText(requireContext(), "Note exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to export note: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                workbook.close()
            }
        }
    }


    private fun navigateToNoteFragment() {
        val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteFragment()
        findNavController().navigate(action)
    }

    private fun getCellValue(cell: Cell): String {
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> ""
        }
    }
}
