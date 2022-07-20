cmake_minimum_required(VERSION 3.21)

# set the project name and version
project(CameraRtcSourceService VERSION 1.0)

# specify the C++ standard
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN 1)

if (CMAKE_HOST_WIN32)
	set(SOURCE_REPOS_DIR "C:\\Users\\49190\\source\\repos")
	
	set(SOCKPP_SRC_DIR "${SOURCE_REPOS_DIR}\\sockpp_src")
	set(SOCKPP_INCLUDE_DIR "${SOCKPP_SRC_DIR}\\sockpp\\include")
	set(SOCKPP_LIBRARY_PATH "${SOCKPP_SRC_DIR}\\sockpp_vs_build\\Debug")
	
	set(WEBRTC_SRC_DIR "${SOURCE_REPOS_DIR}\\webrtc_native_code\\webrtc-checkout\\src")
	set(WEBRTC_INCLUDE_DIR "${WEBRTC_SRC_DIR};${WEBRTC_SRC_DIR}\\third_party\\abseil-cpp;${WEBRTC_SRC_DIR}\\third_party\\libyuv\\include")
	set(WEBRTC_LIBRARY_PATH "${WEBRTC_SRC_DIR}\\out\\msvc\\uwp\\Debug\\x64\\obj")
	add_compile_definitions(USE_AURA=1)
	add_compile_definitions(_HAS_EXCEPTIONS=0)
	add_compile_definitions(__STD_C)
	add_compile_definitions(_CRT_RAND_S)
	add_compile_definitions(_CRT_SECURE_NO_DEPRECATE)
	add_compile_definitions(_SCL_SECURE_NO_DEPRECATE)
	add_compile_definitions(_ATL_NO_OPENGL)
	add_compile_definitions(_WINDOWS)
	add_compile_definitions(CERT_CHAIN_PARA_HAS_EXTRA_FIELDS)
	add_compile_definitions(PSAPI_VERSION=2)
	add_compile_definitions(WIN32)
	add_compile_definitions(_SECURE_ATL)
	add_compile_definitions(WINUWP)
	add_compile_definitions(__WRL_NO_DEFAULT_LIB__)
	add_compile_definitions(WINAPI_FAMILY=WINAPI_FAMILY_PC_APP)
	add_compile_definitions(WIN10=_WIN32_WINNT_WIN10)
	add_compile_definitions(WIN32_LEAN_AND_MEAN)
	add_compile_definitions(NOMINMAX)
	add_compile_definitions(_UNICODE)
	add_compile_definitions(UNICODE)
	add_compile_definitions(NTDDI_VERSION=NTDDI_WIN10_RS2)
	add_compile_definitions(_WIN32_WINNT=0x0A00)
	add_compile_definitions(WINVER=0x0A00)
	add_compile_definitions(NDEBUG)
	add_compile_definitions(NVALGRIND)
	add_compile_definitions(DYNAMIC_ANNOTATIONS_ENABLED=0)
	add_compile_definitions(WEBRTC_ENABLE_PROTOBUF=0)
	add_compile_definitions(WEBRTC_INCLUDE_INTERNAL_AUDIO_DEVICE)
	add_compile_definitions(RTC_ENABLE_VP9)
	add_compile_definitions(HAVE_SCTP)
	add_compile_definitions(WEBRTC_LIBRARY_IMPL)
	add_compile_definitions(WEBRTC_NON_STATIC_TRACE_EVENT_HANDLERS)
	add_compile_definitions(WEBRTC_WIN)
	add_compile_definitions(ABSL_ALLOCATOR_NOTHROW=1)
	add_compile_definitions(HAVE_SCTP)
	add_compile_definitions(WEBRTC_VIDEO_CAPTURE_WINRT)
	
	set(CMAKE_EXE_LINKER_FLAGS /NODEFAULTLIB:\"libcmtd\")
	
	set(WINSDK_DIR "D:\\Windows Kits\\10\\Lib\\10.0.20348.0\\um\\x64")
else()
	set(SOCKPP_SRC_DIR "/home/pi/sockpp_src")
	set(SOCKPP_INCLUDE_DIR "${SOCKPP_SRC_DIR}/sockapp/include")
	set(SOCKPP_LIBRARY_PATH "${SOCKPP_SRC_DIR}/sockpp_build/Debug")
	
	set(WEBRTC_SRCDIR "/home/pi/webrtc_native_code/webrtc-checkout/src")
	set(WEBRTC_INCLUDE_DIR "${WEBRTC_SRC_DIR};${WEBRTC_SRC_DIR}/third_party/abseil-cpp")
	set(WEBRTC_LIBRARY_PATH "${WEBRTC_SRC_DIR}/out/Default/obj")
endif()

# add the executable
add_executable(CameraRtcSourceService
			SocketServer.cxx
			SocketServer.h
			CameraRtcSourceService.cxx
			CameraRtcSourcePeer.cxx
			CameraRtcSourcePeer.h
			)

target_include_directories(CameraRtcSourceService
	PRIVATE ${SOCKPP_INCLUDE_DIR}
	PRIVATE ${WEBRTC_INCLUDE_DIR}
	PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
)

find_library(SOCKPP_LIB NAMES sockpp-static.lib PATHS ${SOCKPP_LIBRARY_PATH})
find_library(WEBRTC_LIB NAMES webrtc.lib PATHS ${WEBRTC_LIBRARY_PATH})

target_link_libraries(CameraRtcSourceService PRIVATE ${SOCKPP_LIB} PRIVATE ${WEBRTC_LIB})

# add the install targets
if (CMAKE_HOST_WIN32)
	set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
	set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)
	
	if (NOT CMAKE_BUILD_TYPE)
		set(CMAKE_BUILD_TYPE "Debug")
		message("Set CMAKE_BUILD_TYPE to default value(Debug). ")
	endif()
	
	install(TARGETS CameraRtcSourceService
			DESTINATION bin
			)
else()
	install(TARGETS CameraRtcSourceService
			DESTINATION bin
			)
endif()
