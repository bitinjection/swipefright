defmodule Api.ContentTest do
  use Api.DataCase

  alias Api.Content

  describe "posts" do
    alias Api.Content.Post

    @valid_attrs %{caption: "some caption", title: "some title"}
    @update_attrs %{caption: "some updated caption", title: "some updated title"}
    @invalid_attrs %{caption: nil, title: nil}

    def post_fixture(attrs \\ %{}) do
      {:ok, post} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Content.create_post()

      post
    end

    test "list_posts/0 returns all posts" do
      post = post_fixture()
      assert Content.list_posts() == [post]
    end

    test "get_post!/1 returns the post with given id" do
      post = post_fixture()
      assert Content.get_post!(post.id) == post
    end

    test "create_post/1 with valid data creates a post" do
      assert {:ok, %Post{} = post} = Content.create_post(@valid_attrs)
      assert post.caption == "some caption"
      assert post.title == "some title"
    end

    test "create_post/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Content.create_post(@invalid_attrs)
    end

    test "update_post/2 with valid data updates the post" do
      post = post_fixture()
      assert {:ok, post} = Content.update_post(post, @update_attrs)
      assert %Post{} = post
      assert post.caption == "some updated caption"
      assert post.title == "some updated title"
    end

    test "update_post/2 with invalid data returns error changeset" do
      post = post_fixture()
      assert {:error, %Ecto.Changeset{}} = Content.update_post(post, @invalid_attrs)
      assert post == Content.get_post!(post.id)
    end

    test "delete_post/1 deletes the post" do
      post = post_fixture()
      assert {:ok, %Post{}} = Content.delete_post(post)
      assert_raise Ecto.NoResultsError, fn -> Content.get_post!(post.id) end
    end

    test "change_post/1 returns a post changeset" do
      post = post_fixture()
      assert %Ecto.Changeset{} = Content.change_post(post)
    end
  end

  describe "images" do
    alias Api.Content.Image

    @valid_attrs %{image: "some image", post_id: 42, sequence: 42, thumbnail: "some thumbnail"}
    @update_attrs %{image: "some updated image", post_id: 43, sequence: 43, thumbnail: "some updated thumbnail"}
    @invalid_attrs %{image: nil, post_id: nil, sequence: nil, thumbnail: nil}

    def image_fixture(attrs \\ %{}) do
      {:ok, image} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Content.create_image()

      image
    end

    test "list_images/0 returns all images" do
      image = image_fixture()
      assert Content.list_images() == [image]
    end

    test "get_image!/1 returns the image with given id" do
      image = image_fixture()
      assert Content.get_image!(image.id) == image
    end

    test "create_image/1 with valid data creates a image" do
      assert {:ok, %Image{} = image} = Content.create_image(@valid_attrs)
      assert image.image == "some image"
      assert image.post_id == 42
      assert image.sequence == 42
      assert image.thumbnail == "some thumbnail"
    end

    test "create_image/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Content.create_image(@invalid_attrs)
    end

    test "update_image/2 with valid data updates the image" do
      image = image_fixture()
      assert {:ok, image} = Content.update_image(image, @update_attrs)
      assert %Image{} = image
      assert image.image == "some updated image"
      assert image.post_id == 43
      assert image.sequence == 43
      assert image.thumbnail == "some updated thumbnail"
    end

    test "update_image/2 with invalid data returns error changeset" do
      image = image_fixture()
      assert {:error, %Ecto.Changeset{}} = Content.update_image(image, @invalid_attrs)
      assert image == Content.get_image!(image.id)
    end

    test "delete_image/1 deletes the image" do
      image = image_fixture()
      assert {:ok, %Image{}} = Content.delete_image(image)
      assert_raise Ecto.NoResultsError, fn -> Content.get_image!(image.id) end
    end

    test "change_image/1 returns a image changeset" do
      image = image_fixture()
      assert %Ecto.Changeset{} = Content.change_image(image)
    end
  end

  describe "posts" do
    alias Api.Content.Post

    @valid_attrs %{caption: "some caption", title: "some title"}
    @update_attrs %{caption: "some updated caption", title: "some updated title"}
    @invalid_attrs %{caption: nil, title: nil}

    def post_fixture(attrs \\ %{}) do
      {:ok, post} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Content.create_post()

      post
    end

    test "list_posts/0 returns all posts" do
      post = post_fixture()
      assert Content.list_posts() == [post]
    end

    test "get_post!/1 returns the post with given id" do
      post = post_fixture()
      assert Content.get_post!(post.id) == post
    end

    test "create_post/1 with valid data creates a post" do
      assert {:ok, %Post{} = post} = Content.create_post(@valid_attrs)
      assert post.caption == "some caption"
      assert post.title == "some title"
    end

    test "create_post/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Content.create_post(@invalid_attrs)
    end

    test "update_post/2 with valid data updates the post" do
      post = post_fixture()
      assert {:ok, post} = Content.update_post(post, @update_attrs)
      assert %Post{} = post
      assert post.caption == "some updated caption"
      assert post.title == "some updated title"
    end

    test "update_post/2 with invalid data returns error changeset" do
      post = post_fixture()
      assert {:error, %Ecto.Changeset{}} = Content.update_post(post, @invalid_attrs)
      assert post == Content.get_post!(post.id)
    end

    test "delete_post/1 deletes the post" do
      post = post_fixture()
      assert {:ok, %Post{}} = Content.delete_post(post)
      assert_raise Ecto.NoResultsError, fn -> Content.get_post!(post.id) end
    end

    test "change_post/1 returns a post changeset" do
      post = post_fixture()
      assert %Ecto.Changeset{} = Content.change_post(post)
    end
  end

  describe "images" do
    alias Api.Content.Image

    @valid_attrs %{image: "some image", sequence: 42, thumbnail: "some thumbnail"}
    @update_attrs %{image: "some updated image", sequence: 43, thumbnail: "some updated thumbnail"}
    @invalid_attrs %{image: nil, sequence: nil, thumbnail: nil}

    def image_fixture(attrs \\ %{}) do
      {:ok, image} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Content.create_image()

      image
    end

    test "list_images/0 returns all images" do
      image = image_fixture()
      assert Content.list_images() == [image]
    end

    test "get_image!/1 returns the image with given id" do
      image = image_fixture()
      assert Content.get_image!(image.id) == image
    end

    test "create_image/1 with valid data creates a image" do
      assert {:ok, %Image{} = image} = Content.create_image(@valid_attrs)
      assert image.image == "some image"
      assert image.sequence == 42
      assert image.thumbnail == "some thumbnail"
    end

    test "create_image/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Content.create_image(@invalid_attrs)
    end

    test "update_image/2 with valid data updates the image" do
      image = image_fixture()
      assert {:ok, image} = Content.update_image(image, @update_attrs)
      assert %Image{} = image
      assert image.image == "some updated image"
      assert image.sequence == 43
      assert image.thumbnail == "some updated thumbnail"
    end

    test "update_image/2 with invalid data returns error changeset" do
      image = image_fixture()
      assert {:error, %Ecto.Changeset{}} = Content.update_image(image, @invalid_attrs)
      assert image == Content.get_image!(image.id)
    end

    test "delete_image/1 deletes the image" do
      image = image_fixture()
      assert {:ok, %Image{}} = Content.delete_image(image)
      assert_raise Ecto.NoResultsError, fn -> Content.get_image!(image.id) end
    end

    test "change_image/1 returns a image changeset" do
      image = image_fixture()
      assert %Ecto.Changeset{} = Content.change_image(image)
    end
  end

  describe "pending" do
    alias Api.Content.PendingPost

    @valid_attrs %{caption: "some caption", image: "some image", title: "some title"}
    @update_attrs %{caption: "some updated caption", image: "some updated image", title: "some updated title"}
    @invalid_attrs %{caption: nil, image: nil, title: nil}

    def pending_post_fixture(attrs \\ %{}) do
      {:ok, pending_post} =
        attrs
        |> Enum.into(@valid_attrs)
        |> Content.create_pending_post()

      pending_post
    end

    test "list_pending/0 returns all pending" do
      pending_post = pending_post_fixture()
      assert Content.list_pending() == [pending_post]
    end

    test "get_pending_post!/1 returns the pending_post with given id" do
      pending_post = pending_post_fixture()
      assert Content.get_pending_post!(pending_post.id) == pending_post
    end

    test "create_pending_post/1 with valid data creates a pending_post" do
      assert {:ok, %PendingPost{} = pending_post} = Content.create_pending_post(@valid_attrs)
      assert pending_post.caption == "some caption"
      assert pending_post.image == "some image"
      assert pending_post.title == "some title"
    end

    test "create_pending_post/1 with invalid data returns error changeset" do
      assert {:error, %Ecto.Changeset{}} = Content.create_pending_post(@invalid_attrs)
    end

    test "update_pending_post/2 with valid data updates the pending_post" do
      pending_post = pending_post_fixture()
      assert {:ok, pending_post} = Content.update_pending_post(pending_post, @update_attrs)
      assert %PendingPost{} = pending_post
      assert pending_post.caption == "some updated caption"
      assert pending_post.image == "some updated image"
      assert pending_post.title == "some updated title"
    end

    test "update_pending_post/2 with invalid data returns error changeset" do
      pending_post = pending_post_fixture()
      assert {:error, %Ecto.Changeset{}} = Content.update_pending_post(pending_post, @invalid_attrs)
      assert pending_post == Content.get_pending_post!(pending_post.id)
    end

    test "delete_pending_post/1 deletes the pending_post" do
      pending_post = pending_post_fixture()
      assert {:ok, %PendingPost{}} = Content.delete_pending_post(pending_post)
      assert_raise Ecto.NoResultsError, fn -> Content.get_pending_post!(pending_post.id) end
    end

    test "change_pending_post/1 returns a pending_post changeset" do
      pending_post = pending_post_fixture()
      assert %Ecto.Changeset{} = Content.change_pending_post(pending_post)
    end
  end
end
