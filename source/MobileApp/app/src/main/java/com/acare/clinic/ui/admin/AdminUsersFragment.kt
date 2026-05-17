package com.acare.clinic.ui.admin

import android.widget.ArrayAdapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.UpdateUserRequest
import com.acare.clinic.data.model.UserProfile
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAdminUsersBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AdminUsersFragment — Quản lý người dùng (Admin).
 * Tương đương Users.jsx trong Frontend web.
 * Gọi: GET /api/users, GET /api/users/search
 */
class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }
    private var searchJob: Job? = null
    private var specialties: List<com.acare.clinic.data.model.Specialty> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUsers()
        loadSpecialties()

        // Tìm kiếm với debounce 500ms
        binding.etSearch.doAfterTextChanged { text ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                val query = text.toString().trim()
                if (query.isEmpty()) loadUsers() else searchUsers(query)
            }
        }

        binding.btnRefresh.setOnClickListener {
            binding.etSearch.text?.clear()
            loadUsers()
        }

        binding.fabAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun loadSpecialties() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getSpecialties()
                specialties = res.body() ?: emptyList()
            } catch (e: Exception) {}
        }
    }

    private fun showAddUserDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_user, null)

        val actRole = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.actRole)
        val actGender = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.actGender)
        val actSpecialty = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.actSpecialty)
        val layoutDoctorInfo = dialogView.findViewById<View>(R.id.layoutDoctorInfo)

        val roles = arrayOf("Bệnh nhân", "Bác sĩ", "Quản trị viên")
        val roleValues = arrayOf("PATIENT", "DOCTOR", "ADMIN")
        val genders = arrayOf("Nam", "Nữ", "Khác")
        val genderValues = arrayOf("MALE", "FEMALE", "OTHER")
        
        actRole.setAdapter(android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, roles))
        actGender.setAdapter(android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genders))
        
        val specialtyNames = specialties.map { "${it.name} (${it.code})" }
        actSpecialty.setAdapter(android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, specialtyNames))

        actRole.setOnItemClickListener { _, _, position, _ ->
            if (roleValues[position] == "DOCTOR") {
                layoutDoctorInfo.visibility = View.VISIBLE
            } else {
                layoutDoctorInfo.visibility = View.GONE
            }
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Thêm người dùng mới")
            .setView(dialogView)
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Thêm") { _, _ ->
                val fullName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etFullName).text.toString().trim()
                val email = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEmail).text.toString().trim()
                val phone = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhone).text.toString().trim()
                val password = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPassword).text.toString().trim()
                
                val roleStr = actRole.text.toString()
                val roleIdx = roles.indexOf(roleStr)
                val roleVal = if (roleIdx >= 0) roleValues[roleIdx] else "PATIENT"
                
                val genderStr = actGender.text.toString()
                val genderIdx = genders.indexOf(genderStr)
                val genderVal = if (genderIdx >= 0) genderValues[genderIdx] else "OTHER"
                
                val birthDate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBirthDate).text.toString().trim()
                val address = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etAddress).text.toString().trim()
                val idNumber = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etIdNumber).text.toString().trim()

                var specialtyId: Long? = null
                var clinicLocation: String? = null
                var workingDays: String? = null
                var shiftStart: String? = null
                var shiftEnd: String? = null

                if (roleVal == "DOCTOR") {
                    val specStr = actSpecialty.text.toString()
                    val specIdx = specialtyNames.indexOf(specStr)
                    if (specIdx >= 0) specialtyId = specialties[specIdx].id
                    
                    clinicLocation = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etClinicLocation).text.toString().trim()
                    shiftStart = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etShiftStart).text.toString().trim()
                    shiftEnd = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etShiftEnd).text.toString().trim()
                    
                    val days = mutableListOf<String>()
                    if (dialogView.findViewById<android.widget.CheckBox>(R.id.cbMon).isChecked) days.add("MONDAY")
                    if (dialogView.findViewById<android.widget.CheckBox>(R.id.cbTue).isChecked) days.add("TUESDAY")
                    if (dialogView.findViewById<android.widget.CheckBox>(R.id.cbWed).isChecked) days.add("WEDNESDAY")
                    if (dialogView.findViewById<android.widget.CheckBox>(R.id.cbThu).isChecked) days.add("THURSDAY")
                    if (dialogView.findViewById<android.widget.CheckBox>(R.id.cbFri).isChecked) days.add("FRIDAY")
                    workingDays = days.joinToString(",")
                }

                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    Snackbar.make(binding.root, "Vui lòng nhập đủ thông tin bắt buộc", Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                if (roleVal == "DOCTOR") {
                    if (specialtyId == null) {
                        Snackbar.make(binding.root, "Vui lòng chọn Chuyên khoa cho Bác sĩ", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    if (clinicLocation.isNullOrEmpty()) {
                        Snackbar.make(binding.root, "Vui lòng nhập Phòng khám cho Bác sĩ", Snackbar.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                }

                val request = com.acare.clinic.data.model.RegisterRequest(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    password = password,
                    confirmPassword = password,
                    role = roleVal,
                    gender = genderVal,
                    birthDate = birthDate.ifEmpty { null },
                    address = address,
                    idNumber = idNumber.ifEmpty { null },
                    specialtyId = specialtyId,
                    clinicLocation = clinicLocation?.ifEmpty { null },
                    workingDays = workingDays,
                    shiftStart = shiftStart?.ifEmpty { null },
                    shiftEnd = shiftEnd?.ifEmpty { null }
                )

                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.createUser(request)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Thêm người dùng thành công", Snackbar.LENGTH_SHORT).show()
                                loadUsers()
                            } else {
                                val errorMsg = try {
                                    val errorJson = org.json.JSONObject(res.errorBody()?.string() ?: "")
                                    errorJson.optString("message", "Lỗi không xác định")
                                } catch (e: Exception) {
                                    "Lỗi không xác định"
                                }
                                Snackbar.make(binding.root, "Lỗi: $errorMsg", Snackbar.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Lỗi Exception: ${e.message}", Snackbar.LENGTH_LONG).show()
                        }
                    } finally {
                        if (_binding != null) {
                            setLoading(false)
                        }
                    }
                }
            }
            .show()
    }

    private fun loadUsers() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getUsers()
                val list = res.body() ?: emptyList()
                if (_binding != null) {
                    displayUsers(list)
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    Snackbar.make(binding.root, "Không thể tải danh sách", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null) {
                    setLoading(false)
                }
            }
        }
    }

    private fun searchUsers(keyword: String) {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.searchUsers(keyword)
                val list = res.body() ?: emptyList()
                if (_binding != null) {
                    displayUsers(list)
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    Snackbar.make(binding.root, "Lỗi tìm kiếm", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null) {
                    setLoading(false)
                }
            }
        }
    }

    private fun displayUsers(users: List<UserProfile>) {
        binding.tvUserCount.text = "${users.size} người dùng"
        if (users.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvUsers.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvUsers.visibility = View.VISIBLE
            binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
            binding.rvUsers.adapter = UserCardAdapter(users, this)
        }
    }

    fun showEditUserDialog(user: UserProfile) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_user, null)

        dialogView.findViewById<TextInputEditText>(R.id.etFullName).setText(user.fullName ?: "")
        dialogView.findViewById<TextInputEditText>(R.id.etPhone).setText(user.phone ?: "")
        dialogView.findViewById<TextInputEditText>(R.id.etBirthDate).setText(user.birthDate ?: "")
        dialogView.findViewById<TextInputEditText>(R.id.etAddress).setText(user.address ?: "")

        val actGender = dialogView.findViewById<android.widget.AutoCompleteTextView>(R.id.actGender)
        val genders = arrayOf("Nam", "Nữ", "Khác")
        val genderValues = arrayOf("MALE", "FEMALE", "OTHER")
        actGender.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genders))
        val currentGenderIdx = genderValues.indexOf(user.gender ?: "OTHER")
        if (currentGenderIdx >= 0) actGender.setText(genders[currentGenderIdx], false)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chỉnh sửa: ${user.fullName ?: "User"}")
            .setView(dialogView)
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setNeutralButton("Xóa") { _, _ -> confirmDeleteUser(user) }
            .setPositiveButton("Cập nhật") { _, _ ->
                val fullName = dialogView.findViewById<TextInputEditText>(R.id.etFullName).text.toString().trim()
                val phone = dialogView.findViewById<TextInputEditText>(R.id.etPhone).text.toString().trim()
                val birthDate = dialogView.findViewById<TextInputEditText>(R.id.etBirthDate).text.toString().trim()
                val address = dialogView.findViewById<TextInputEditText>(R.id.etAddress).text.toString().trim()

                val genderStr = actGender.text.toString()
                val genderIdx = genders.indexOf(genderStr)
                val genderVal = if (genderIdx >= 0) genderValues[genderIdx] else user.gender

                val request = UpdateUserRequest(
                    fullName = fullName.ifEmpty { null },
                    email = user.email,
                    phone = phone.ifEmpty { null },
                    role = user.role,
                    gender = genderVal,
                    birthDate = birthDate.ifEmpty { null },
                    address = address.ifEmpty { null },
                    idNumber = user.idNumber
                )

                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.updateUser(user.id, request)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Cập nhật thành công", Snackbar.LENGTH_SHORT).show()
                                loadUsers()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi cập nhật", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) Snackbar.make(binding.root, "Lỗi: ${e.message}", Snackbar.LENGTH_LONG).show()
                    } finally {
                        if (_binding != null) setLoading(false)
                    }
                }
            }
            .show()
    }

    private fun confirmDeleteUser(user: UserProfile) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa người dùng")
            .setMessage("Bạn có chắc chắn muốn xóa \"${user.fullName ?: user.email}\"?")
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Xóa") { _, _ ->
                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.deleteUser(user.id)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Đã xóa người dùng", Snackbar.LENGTH_SHORT).show()
                                loadUsers()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi xóa", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) Snackbar.make(binding.root, "Lỗi: ${e.message}", Snackbar.LENGTH_LONG).show()
                    } finally {
                        if (_binding != null) setLoading(false)
                    }
                }
            }
            .show()
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/** Adapter nội bộ cho danh sách user */
class UserCardAdapter(
    private val items: List<UserProfile>,
    private val fragment: AdminUsersFragment
) : RecyclerView.Adapter<UserCardAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val chipRole: Chip = view.findViewById(R.id.chipRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_card, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = items[position]
        holder.tvInitial.text = user.fullName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        holder.tvName.text = user.fullName ?: "Chưa cập nhật"
        holder.tvEmail.text = user.email ?: "Chưa cập nhật"
        holder.chipRole.text = when (user.role) {
            "PATIENT" -> "Bệnh nhân"
            "DOCTOR"  -> "Bác sĩ"
            "ADMIN"   -> "Quản trị"
            else      -> user.role ?: "Chưa rõ"
        }
        holder.chipRole.setChipBackgroundColorResource(
            when (user.role) {
                "DOCTOR" -> R.color.primary
                "ADMIN"  -> R.color.warning
                else     -> R.color.success
            }
        )

        // Tap to edit user
        holder.itemView.setOnClickListener {
            fragment.showEditUserDialog(user)
        }
    }
}
