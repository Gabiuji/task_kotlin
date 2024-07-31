package com.example.appkotlines.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appkotlines.R
import com.example.appkotlines.databinding.FragmentDoneBinding
import com.example.appkotlines.databinding.FragmentHomeBinding
import com.example.appkotlines.ui.helper.FirebaseHelper
import com.example.appkotlines.ui.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClicks()
        getTask()
    }
    private fun initClicks(){
        binding.fabAddDone.setOnClickListener{
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }
    private fun getTask(){
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser()?:"")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        taskList.clear()
                        for(snap in snapshot.children){
                            val task = snap.getValue(Task::class.java) as Task
                            if(task.status==2) taskList.add(task)
                        }
                        binding.progressbar.isVisible=false
                        binding.textInfo.text=""
                        taskList.reverse()
                        initAdapter()
                    }
                    else{
                        binding.textInfo.text="Nenhuma tarefa cadastrada"
                    }
                    binding.progressbar.isVisible=false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun initAdapter(){
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList){task, select->
            optionSelect(task, select)
        }
        binding.rvTask.adapter=taskAdapter
    }
    private fun optionSelect(task: Task, select: Int){
        when(select){
            taskAdapter.SELECT_REMOVE ->{
                deleteTask(task)
            }
            taskAdapter.SELECT_EDIT ->{
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            taskAdapter.SELECT_BACK->{
                task.status = 1
                updateTask(task)
            }
        }
    }
    private fun updateTask(task: Task){
        FirebaseHelper.getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser() ?:"")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener{ task->
                if(task.isSuccessful){
                    Toast.makeText(
                        requireContext(),
                        "Tarefa atualizada com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                } else{
                    Toast.makeText(
                        requireContext(), "Erro ao salvar tarefa", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener{
                binding.progressbar.isVisible = false
                Toast.makeText(requireContext(), "Erro ao salvar a tarefa", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTask(task: Task){
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getIdUser()?:"")
            .child(task.id)
            .removeValue()
        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}