package io.github.nullptrx.files.settings

import android.content.SharedPreferences
import android.os.Parcel
import androidx.annotation.AnyRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import io.github.nullptrx.files.app.appClassLoader
import io.github.nullptrx.files.app.application
import io.github.nullptrx.files.extension.use
import io.github.nullptrx.files.util.Base64
import io.github.nullptrx.files.util.asBase64
import io.github.nullptrx.files.util.toBase64
import io.github.nullptrx.files.util.toByteArray


class ParcelValueSettingLiveData<T>(
  nameSuffix: String?,
  @StringRes keyRes: Int,
  keySuffix: String?,
  private val defaultValue: T
) : SettingLiveData<T>(nameSuffix, keyRes, keySuffix, 0) {
  constructor(@StringRes keyRes: Int, defaultValue: T) : this(null, keyRes, null, defaultValue)

  init {
    init()
  }

  override fun getDefaultValue(@AnyRes defaultValueRes: Int): T = defaultValue

  override fun getValue(
    sharedPreferences: SharedPreferences,
    key: String,
    defaultValue: T
  ): T =
    try {
      sharedPreferences.getString(key, null)?.asBase64()?.toParcelValue()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    } ?: defaultValue

  override fun putValue(sharedPreferences: SharedPreferences, key: String, value: T) {
    sharedPreferences.edit { putString(key, value?.toParcelBase64()?.value) }
  }

  private fun Base64.toParcelValue(): T {
    val bytes = toByteArray()
    return Parcel.obtain().use { parcel ->
      parcel.unmarshall(bytes, 0, bytes.size)
      parcel.setDataPosition(0)
      @Suppress("UNCHECKED_CAST")
      parcel.readValue(appClassLoader) as T
    }
  }

  private fun T.toParcelBase64(): Base64 {
    val bytes = Parcel.obtain().use { parcel ->
      parcel.writeValue(this)
      parcel.marshall()
    }
    return bytes.toBase64()
  }
}


// Use string resource for default value so that we can support ListPreference.
class EnumSettingLiveData<E : Enum<E>>(
  nameSuffix: String?,
  @StringRes keyRes: Int,
  keySuffix: String?,
  @StringRes defaultValueRes: Int,
  enumClass: Class<E>
) : SettingLiveData<E>(nameSuffix, keyRes, keySuffix, defaultValueRes) {
  private val enumValues = enumClass.enumConstants!!

  constructor(
    @StringRes keyRes: Int,
    @StringRes defaultValueRes: Int,
    enumClass: Class<E>
  ) : this(null, keyRes, null, defaultValueRes, enumClass)

  init {
    init()
  }

  override fun getDefaultValue(@StringRes defaultValueRes: Int): E =
    enumValues[application.getString(defaultValueRes).toInt()]

  override fun getValue(
    sharedPreferences: SharedPreferences,
    key: String,
    defaultValue: E
  ): E {
    val valueOrdinal = sharedPreferences.getString(key, null)?.toInt() ?: return defaultValue
    return if (valueOrdinal in enumValues.indices) enumValues[valueOrdinal] else defaultValue
  }

  override fun putValue(sharedPreferences: SharedPreferences, key: String, value: E) {
    sharedPreferences.edit { putString(key, value.ordinal.toString()) }
  }
}


class StringSettingLiveData(
  nameSuffix: String?,
  @StringRes keyRes: Int,
  keySuffix: String?,
  @StringRes defaultValueRes: Int
) : SettingLiveData<String>(nameSuffix, keyRes, keySuffix, defaultValueRes) {
  constructor(@StringRes keyRes: Int, @StringRes defaultValueRes: Int) : this(
    null, keyRes, null, defaultValueRes
  )

  init {
    init()
  }

  override fun getDefaultValue(@StringRes defaultValueRes: Int): String =
    application.getString(defaultValueRes)

  override fun getValue(
    sharedPreferences: SharedPreferences,
    key: String,
    defaultValue: String
  ): String = sharedPreferences.getString(key, defaultValue)!!

  override fun putValue(sharedPreferences: SharedPreferences, key: String, value: String) {
    sharedPreferences.edit { putString(key, value) }
  }
}